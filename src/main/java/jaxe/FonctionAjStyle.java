/*
 * Jaxe - Editeur XML en Java
 * 
 * Copyright (C) 2002 Observatoire de Paris-Meudon
 * 
 * Ce programme est un logiciel libre ; vous pouvez le redistribuer et/ou le
 * modifier conformément aux dispositions de la Licence Publique Générale GNU,
 * telle que publiée par la Free Software Foundation ; version 2 de la licence,
 * ou encore (à votre choix) toute version ultérieure.
 * 
 * Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS AUCUNE
 * GARANTIE ; sans même la garantie implicite de COMMERCIALISATION ou
 * D'ADAPTATION A UN OBJET PARTICULIER. Pour plus de détail, voir la Licence
 * Publique Générale GNU .
 * 
 * Vous devez avoir reçu un exemplaire de la Licence Publique Générale GNU en
 * même temps que ce programme ; si ce n'est pas le cas, écrivez à la Free
 * Software Foundation Inc., 675 Mass Ave, Cambridge, MA 02139, Etats-Unis.
 */

package jaxe;

import org.apache.log4j.Logger;

import javax.swing.text.BadLocationException;
import javax.swing.text.Position;

import jaxe.elements.JEStyle;
import jaxe.elements.JETexte;

import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * Fonction permettant d'appliquer un style sur une zone du document.
 * La référence de l'élément correspondant au style ajouté est passé en paramètre du constructeur.
 */
public class FonctionAjStyle implements Fonction {
    /**
     * Logger for this class
     */
    private static final Logger LOG = Logger.getLogger(FonctionAjStyle.class);
    
    private final Element refStyle; // référence de l'élément JEStyle
    
    
    /**
     * Attention, contrairement à ce qui est expliqué dans Fonction, ce constructeur ne prend pas en paramètre
     * la définition d'un menu, mais la référence d'un élément.
     */
    public FonctionAjStyle(final Element refElement) {
        refStyle = refElement;
    }
    
    public boolean appliquer(final JaxeDocument doc, final int start, final int end) {
        boolean done = false;
        try {
            
            // coupure éventuelle des styles et des textes à gauche et à droite
            JaxeElement firstel = doc.rootJE.elementA(start);
            if (firstel == null)
                return(false);
            
            doc.textPane.debutEditionSpeciale(JaxeResourceBundle.getRB().getString("style.Style"), false);
            
           while (firstel.getParent() instanceof JEStyle)
                firstel = firstel.getParent();
            if ((firstel instanceof JEStyle || firstel instanceof JETexte) && firstel.debut.getOffset() < start)
                firstel = firstel.couper(doc.createPosition(start));
            final JaxeElement p1 = firstel.getParent();
            JaxeElement lastel = doc.rootJE.elementA(end - 1);
            while (lastel != null && lastel.getParent() instanceof JEStyle)
                lastel = lastel.getParent();
            if ((lastel instanceof JEStyle || lastel instanceof JETexte) && lastel.debut.getOffset() < end && lastel.fin.getOffset() >= end)
                lastel.couper(doc.createPosition(end));
            
            JaxeElement je = firstel;
            Node next;
            // pour tous les éléments dans la sélection
            while (je != null) {
                next = je.noeud.getNextSibling();
                if (!(je instanceof JEStyle) || !dejaApplique((JEStyle)je, refStyle))
                    tostyle(je, refStyle);
                
                done = true;
                if (next == null || je == lastel)
                    je = null;
                else
                    je = doc.getElementForNode(next);
            }
            
            p1.regrouperTextes();
        } catch (final BadLocationException ex) {
            LOG.error("appliquer(JaxeDocument, int, int) - BadLocationException", ex);
        }
        doc.textPane.finEditionSpeciale();
        return done;
    }
    
    /**
     * Applique le style à un JEStyle ou un JETexte
     */
    private static boolean tostyle(final JaxeElement je, final Element refStyle) throws BadLocationException {
        boolean done = false;
        // on ajoute le noeud du style le plus bas possible dans l'arbre (c'est à dire au-dessus du texte), à chaque fois qu'il n'existe pas déjà
        final JaxeDocument doc = je.doc;
        if (je instanceof JETexte) {
            Config conf = doc.cfg.getRefConf(refStyle);
            if (conf == null)
                conf = doc.cfg;
            final Element parentref = refParentConfig(doc, je.getParent(), conf);
            // si le refStyle est un enfant autorisé du parent de je ou l'ancêtre de même config
            if (parentref != null && conf.estSousElement(parentref, refStyle)) {
                // suppression du texte et ajout du style avec le texte dessous
                final int offsetdebut = je.debut.getOffset();
                Node newel = JaxeElement.nouvelElementDOM(doc, refStyle);
                newel.appendChild(je.noeud.cloneNode(false));
                JaxeElement parent = je.getParent();
                while (parent instanceof JEStyle) {
                    // ajout de tous les styles parents (la suppression va les effacer)
                    final Node newel2 = parent.noeud.cloneNode(false);
                    newel2.appendChild(newel);
                    newel = newel2;
                    parent = parent.getParent();
                }
                JaxeUndoableEdit jedit = new JaxeUndoableEdit(JaxeUndoableEdit.SUPPRIMER, je, false);
                jedit.doit();
                final JEStyle newje = new JEStyle(doc);
                newje.noeud = newel;
                newje.debut = doc.createPosition(offsetdebut);
                newje.fin = null;
                jedit = new JaxeUndoableEdit(JaxeUndoableEdit.AJOUTER, newje, false);
                jedit.doit();
            }
        } else if (je.getFirstChild() != null) {
            if (je.getFirstChild().getNextSibling() == null)
                return(tostyle(je.getFirstChild(), refStyle));
            for (Node n = je.noeud.getFirstChild(); n != null; ) {
                final Node next = n.getNextSibling();
                final JaxeElement je2 = doc.getElementForNode(n);
                if (je2 instanceof JETexte || (je2 instanceof JEStyle && !dejaApplique((JEStyle)je2, refStyle)))
                    tostyle(je2, refStyle);
                n = next;
            }
        }
        return(true);
    }

    /**
     * Renvoie la référence du JaxeElement je s'il est dans la config conf, ou du parent de je dans conf sinon
     */
    private static Element refParentConfig(final JaxeDocument doc, final JaxeElement je, final Config conf) {
        Element parentref = null;
        Element parentns = (Element)je.noeud;
        // si la config de je est différente de la config conf
        if (doc.cfg.getElementConf(parentns) != conf)
            // Cherche le premier élément ancêtre de même config
            parentns = doc.cfg.chercheParentConfig(parentns, conf);
        if (parentns != null)
            parentref = doc.getElementForNode(parentns).refElement;
        return(parentref);
    }
    
    /**
     * Renvoie true si l'élément de référence refStyle est dans le JEStyle
     */
    private static boolean dejaApplique(final JEStyle js, final Element refStyle) {
        // done <- true si le style est déjà appliqué dans firstel
        if (js.refElement == refStyle)
            return(true);
        JaxeElement je = js;
        Node n = je.noeud.getFirstChild();
        while (n != null) {
            if (!(n instanceof Element))
                return(false);
            je = js.doc.getElementForNode(n);
            if (je == null || je.debut.getOffset() != js.debut.getOffset() || je.fin.getOffset() != js.fin.getOffset())
                return(false);
            if (je.refElement == refStyle)
                return(true);
            n = je.noeud.getFirstChild();
        }
        return(false);
    }
}
