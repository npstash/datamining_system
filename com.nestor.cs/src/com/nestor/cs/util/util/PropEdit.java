//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// PropEdit.java (nes.util)
// Utworzono 2005-08-11 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.util.util;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.AbstractTableModel;

import com.nestor.cs.util.Messages;

public class PropEdit extends JFrame implements ActionListener {

    private class Model extends AbstractTableModel {

        public int getColumnCount() {
            return 2;
        }

        public int getRowCount() {
            return PropEdit.this.props[0].size();
        }

        public Object getValueAt(int r, int c) {
            return PropEdit.this.props[c].get(r);
        }

        @Override
        public boolean isCellEditable(int r, int c) {
            return (c == 1);
        }

        @Override
        public void setValueAt(Object o, int r, int c) {
            PropEdit.this.props[c].set(r, o.toString());
        }
    }

    private static char[] buf = new char[1024];

    private static final char[] hexDigit = { '0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    public static String codeString(String toCode) {
        String s = saveConvert(toCode, false);
        return s;
    }

    public static String decodeString(String toDecode) {
        String s = loadConvert(toDecode.toCharArray(), 0, toDecode.length(),
                buf);
        return s;
    }

    /*
     * Converts encoded &#92;uxxxx to unicode chars and changes special saved
     * chars to their original forms
     */
    private static String loadConvert(char[] in, int off, int len,
            char[] convtBuf) {
        if (convtBuf.length < len) {
            int newLen = len * 2;
            if (newLen < 0) {
                newLen = Integer.MAX_VALUE;
            }
            convtBuf = new char[newLen];
        }
        char aChar;
        char[] out = convtBuf;
        int outLen = 0;
        int end = off + len;

        while (off < end) {
            aChar = in[off++];
            if (aChar == '\\') {
                aChar = in[off++];
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = in[off++];
                        switch (aChar) {
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            value = (value << 4) + aChar - '0';
                            break;
                        case 'a':
                        case 'b':
                        case 'c':
                        case 'd':
                        case 'e':
                        case 'f':
                            value = (value << 4) + 10 + aChar - 'a';
                            break;
                        case 'A':
                        case 'B':
                        case 'C':
                        case 'D':
                        case 'E':
                        case 'F':
                            value = (value << 4) + 10 + aChar - 'A';
                            break;
                        default:
                            throw new IllegalArgumentException(
                                    "Malformed \\uxxxx encoding."); //$NON-NLS-1$
                        }
                    }
                    out[outLen++] = (char) value;
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    out[outLen++] = aChar;
                }
            } else {
                out[outLen++] = aChar;
            }
        }
        return new String(out, 0, outLen);
    }

    public static void main(String[] argv) throws Exception {
        if (argv.length < 1)
            return;
        File i = new File("."); //$NON-NLS-1$
        System.out
                .println(Messages.getString("PropEdit.1") + i.getAbsolutePath()); //$NON-NLS-1$
        (new PropEdit(new File(argv[0]))).setVisible(true);
    }

    /*
     * Converts unicodes to encoded &#92;uxxxx and escapes special characters
     * with a preceding slash
     */
    private static String saveConvert(String theString, boolean escapeSpace) {
        int len = theString.length();
        int bufLen = len * 2;
        if (bufLen < 0) {
            bufLen = Integer.MAX_VALUE;
        }
        StringBuffer outBuffer = new StringBuffer(bufLen);

        for (int x = 0; x < len; x++) {
            char aChar = theString.charAt(x);
            // Handle common case first, selecting largest block that
            // avoids the specials below
            if ((aChar > 61) && (aChar < 127)) {
                if (aChar == '\\') {
                    outBuffer.append('\\');
                    outBuffer.append('\\');
                    continue;
                }
                outBuffer.append(aChar);
                continue;
            }
            switch (aChar) {
            case ' ':
                if (x == 0 || escapeSpace)
                    outBuffer.append('\\');
                outBuffer.append(' ');
                break;
            case '\t':
                outBuffer.append('\\');
                outBuffer.append('t');
                break;
            case '\n':
                outBuffer.append('\\');
                outBuffer.append('n');
                break;
            case '\r':
                outBuffer.append('\\');
                outBuffer.append('r');
                break;
            case '\f':
                outBuffer.append('\\');
                outBuffer.append('f');
                break;
            case '=': // Fall through
            case ':': // Fall through
            case '#': // Fall through
            case '!':
                outBuffer.append('\\');
                outBuffer.append(aChar);
                break;
            default:
                if ((aChar < 0x0020) || (aChar > 0x007e)) {
                    outBuffer.append('\\');
                    outBuffer.append('u');
                    outBuffer.append(toHex((aChar >> 12) & 0xF));
                    outBuffer.append(toHex((aChar >> 8) & 0xF));
                    outBuffer.append(toHex((aChar >> 4) & 0xF));
                    outBuffer.append(toHex(aChar & 0xF));
                } else {
                    outBuffer.append(aChar);
                }
            }
        }
        return outBuffer.toString();
    }

    private static char toHex(int nibble) {
        return hexDigit[(nibble & 0xF)];
    }

    private int propNum;

    private ArrayList[] props = new ArrayList[2];

    public PropEdit(File f) throws Exception {
        super(Messages.getString("PropEdit.2")); //$NON-NLS-1$

        this.props[0] = new ArrayList();
        this.props[1] = new ArrayList();

        BufferedReader br = new BufferedReader(new FileReader(f));
        this.propNum = 0;
        while (br.ready()) {
            String p = br.readLine();
            int split = p.indexOf("="); //$NON-NLS-1$
            if (split < 0) {
                this.props[0].add(p);
                this.props[1].add(""); //$NON-NLS-1$
            } else {
                this.props[0].add(p.substring(0, split));
                String cod = p.substring(split + 1, p.length());
                this.props[1].add(decodeString(cod));
            }
            this.propNum++;
        }
        br.close();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JTable t = new JTable(new Model());
        JPanel jp = new JPanel(new BorderLayout());
        jp.add(new JScrollPane(t), BorderLayout.CENTER);
        JButton jbSave = new JButton(Messages.getString("PropEdit.5")); //$NON-NLS-1$
        jbSave.addActionListener(this);
        jp.add(jbSave, BorderLayout.SOUTH);
        setContentPane(jp);
        setSize(800, 500);
    }

    public void actionPerformed(ActionEvent arg0) {
        File out = new File("edited.properties"); //$NON-NLS-1$
        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(out));
            for (int i = 0; i < this.props[0].size(); i++) {
                String n = (String) this.props[0].get(i);
                String v = (String) this.props[1].get(i);
                pw.println(n + "=" + codeString(v)); //$NON-NLS-1$
            }
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}