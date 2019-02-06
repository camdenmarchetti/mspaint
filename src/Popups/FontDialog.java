package Popups;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.font.TextAttribute;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;

import static java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment;

public class FontDialog extends JDialog {

    private static final String _title = "Font";
    private static final ModalityType _modalityType = ModalityType.MODELESS;
    private static final Font _DEFAULT_FONT_ = new Font("Arial", Font.PLAIN, 8);

    private static FontDialog _instance = null;
    private static Integer[] _validPoints = { 8, 9, 10, 11, 12, 14, 16, 18, 20, 22, 24, 26, 28, 36, 48, 72 };
    private static final Map<TextAttribute, Integer> _underlineAttribute;
    static
    {
        _underlineAttribute = new HashMap<>();
        _underlineAttribute.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
    }

    private JScrollPane _fonts, _points;
    private JComboBox<Font> _fontDropdown;
    private JComboBox<Integer> _pointDropdown;

    private JButton _bold, _italic, _underline;

    private Font _selected = null;
    private boolean _bolded, _italicised, _underlined;

    public FontDialog() {

        super();
        this.setTitle(this._title);
        this.setModalityType(this._modalityType);

        this._selected = _DEFAULT_FONT_;
        FontDialog._instance = this;

        this.setLayout(new GridBagLayout());
        GridBagConstraints layout = new GridBagConstraints();

        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        String[] fontNameList = getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        Font[] fontList = new Font[fontNameList.length];

        Arrays.sort(fontNameList);
        int fontIndex = 0;
        for (String fontName : fontNameList) {
            fontList[fontIndex] = new Font(fontName, Font.PLAIN, 8);
            fontIndex++;
        }


        this._fontDropdown = new JComboBox<Font>(fontList);
        this._fontDropdown.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value != null) {
                    Font font = (Font) value;
                    value = font.getName();
                }

                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        this._fontDropdown.addActionListener(ae -> {
            int currentStyle = FontDialog.this._selected.getStyle();
            int currentSize = FontDialog.this._selected.getSize();

            Object selection = _fontDropdown.getSelectedItem();
            if (selection != null) {
                FontDialog.this._selected = new Font(((Font)selection).getFontName(), currentStyle, currentSize);
            }
        });

        this._fonts = new JScrollPane(this._fontDropdown);


        this._pointDropdown = new JComboBox<>(_validPoints);
        this._pointDropdown.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value != null) {
                    value = value.toString();
                }

                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        this._pointDropdown.addActionListener(ae -> {
            Object selection = _pointDropdown.getSelectedItem();
            if (selection != null) {
                FontDialog.this._selected = FontDialog.this._selected.deriveFont((float)Integer.valueOf(selection.toString()));
            }
        });

        this._points = new JScrollPane(this._pointDropdown);

        this._bold = new JButton("B");
        this._bold.setFont(this._bold.getFont().deriveFont(Font.BOLD));
        this._bold.setForeground(Color.GRAY);
        this._bold.setFocusPainted(false);
        this._bold.addActionListener(ae -> {
            JButton source = (JButton)ae.getSource();
            FontDialog.this._bolded = !FontDialog.this._bolded;
            if (FontDialog.this._bolded) {
                source.setForeground(Color.BLACK);
            } else {
                source.setForeground(Color.GRAY);
            }

            EventQueue.invokeLater(() -> {
                source.revalidate();
                source.repaint();
            });
        });

        this._italic = new JButton("I");
        this._italic.setFont(this._italic.getFont().deriveFont(Font.ITALIC));
        this._italic.setForeground(Color.GRAY);
        this._italic.setFocusPainted(false);
        this._italic.addActionListener(ae -> {
            JButton source = (JButton)ae.getSource();
            FontDialog.this._italicised = !FontDialog.this._italicised;
            if (FontDialog.this._italicised) {
                source.setForeground(Color.BLACK);
            } else {
                source.setForeground(Color.GRAY);
            }

            EventQueue.invokeLater(() -> {
                source.revalidate();
                source.repaint();
            });
        });

        this._underline = new JButton("U");
        this._underline.setFont(this._underline.getFont().deriveFont(_underlineAttribute));
        this._underline.setForeground(Color.GRAY);
        this._underline.setFocusPainted(false);
        this._underline.addActionListener(ae -> {
            JButton source = (JButton)ae.getSource();
            FontDialog.this._underlined = !FontDialog.this._underlined;
            if (FontDialog.this._underlined) {
                source.setForeground(Color.BLACK);
            } else {
                source.setForeground(Color.GRAY);
            }

            EventQueue.invokeLater(() -> {
                source.revalidate();
                source.repaint();
            });
        });

        List<Font> fontListAsList = Arrays.asList(fontList);
        Optional<Font> sysDefault = fontListAsList.stream().filter(F -> F.getFontName().equalsIgnoreCase(_DEFAULT_FONT_.getFontName())).findFirst();
        sysDefault.ifPresent(font -> this._fontDropdown.setSelectedIndex(fontListAsList.indexOf(font)));

        layout.gridwidth = 6;
        layout.fill = GridBagConstraints.HORIZONTAL;
        add(this._fonts, layout);

        layout.gridx = 6;
        layout.gridwidth = 3;
        add(this._points, layout);

        layout.gridx = 9;
        layout.gridwidth = 1;
        add(this._bold, layout);

        layout.gridx = 10;
        add(this._italic, layout);

        layout.gridx = 11;
        add(this._underline, layout);

        this.pack();
        this.setResizable(false);
    }

    public static boolean isCreated() {
        return FontDialog._instance != null;
    }

    public static FontDialog getCurrent() {
        return FontDialog._instance;
    }

    public static Font GetFont() {
        if (FontDialog._instance == null) {
            return FontDialog._DEFAULT_FONT_;
        }

        Font current = FontDialog._instance._selected;
        if (current == null) {
            return FontDialog._DEFAULT_FONT_;
        }

        if (FontDialog._instance._bolded) {
            current = current.deriveFont(Font.BOLD);
        }

        if (FontDialog._instance._italicised) {
            current = current.deriveFont(Font.ITALIC);
        }

        if (FontDialog._instance._underlined) {
            current = current.deriveFont(_underlineAttribute);
        }

        return current;
    }

    public static void disposeCurrent() {
        if (isCreated()) {
            try {
                FontDialog._instance.dispose();
                FontDialog._instance = null;
            } catch (Exception e) {}
        }
    }
}
