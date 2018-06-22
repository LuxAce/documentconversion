package xmleditorkit;

import javax.swing.*;
import javax.swing.text.BadLocationException;

import com.datascience9.doc.util.FileUtils;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.charset.Charset;
import java.awt.event.ActionEvent;
import java.awt.*;

public class App extends JFrame {
    JEditorPane edit = new JEditorPane();
    public static final String testXML="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
//            "<!DOCTYPE demo PUBLIC \"demo/DTD\" \"file://c:/demo.com/demo.dtd\">"+
            "<!--\n" +
            "  Comments" +
            "-->"+
            "<document name='test' lang='en'>\n" +
            "<meta title='test'/>\n" +
            "<!--\n" +
            "  Body started\n" +
            "-->"+
            "<body>\n" +
            "<paragraph align='left'>\n" +
            "<text color='#000000'>" +
            "Plain text" +
            "</text>\n" +
            "</paragraph>\n" +
            "</body>\n" +
            "</document>";
    
    public App() {
        super("XMLEditorKit example");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        edit.setEditorKit(new XMLEditorKit());
        try {
					String s = FileUtils.readFile2String("/media/paul/workspace/pdftest/334566B59C9340B78ED202A31F4E7B15/result.xml", Charset.defaultCharset());
					System.out.println(s);
					edit.setText(s);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        
//        edit.setEditable(false);

        this.getContentPane().add(new JScrollPane(edit));

        this.setSize(620, 450);
        this.setLocationRelativeTo(null);

    }

    public static void main(String[] args) {
        App m = new App();
        m.setVisible(true);
    }
}
