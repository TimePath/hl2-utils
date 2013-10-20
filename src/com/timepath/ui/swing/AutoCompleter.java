package com.timepath.ui.swing;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

/**
 *
 * @author TimePath
 */
@SuppressWarnings("serial")
public abstract class AutoCompleter {

    private static final String COMPLETION = "AUTOCOMPLETION";

    private static final Logger LOG = Logger.getLogger(AutoCompleter.class.getName());

    //<editor-fold defaultstate="collapsed" desc="Actions">
    private static final Action acceptAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            JComponent tf = (JComponent) e.getSource();
            AutoCompleter completer = (AutoCompleter) tf.getClientProperty(COMPLETION);
            completer.getPopup().setVisible(false);
            completer.acceptedListItem(completer.getList().getSelectedValue());
        }
    };

    private static final Action hideAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            JComponent tf = (JComponent) e.getSource();
            AutoCompleter completer = (AutoCompleter) tf.getClientProperty(COMPLETION);
            if(tf.isEnabled()) {
                completer.getPopup().setVisible(false);
            }
        }
    };

    private static final Action showAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            JComponent tf = (JComponent) e.getSource();
            AutoCompleter completer = (AutoCompleter) tf.getClientProperty(COMPLETION);
            if(tf.isEnabled()) {
                if(!completer.getPopup().isVisible()) {
                    completer.showPopup();
                }
            }
        }
    };

    private static Action shift(final int val) {
        return new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                JComponent tf = (JComponent) e.getSource();
                AutoCompleter completer = (AutoCompleter) tf.getClientProperty(COMPLETION);
                if(tf.isEnabled()) {
                    if(completer.getPopup().isVisible()) {
                        completer.shiftSelection(val);
                    }
                }
            }
        };
    }
    //</editor-fold>

    private final DocumentListener documentListener = new DocumentListener() {
        public void changedUpdate(DocumentEvent e) {
        }
        
        public void insertUpdate(DocumentEvent e) {
            showPopup();
        }
        
        public void removeUpdate(DocumentEvent e) {
            showPopup();
        }
    };

    private final JPopupMenu popup = new JPopupMenu() {
        {
            JScrollPane scroll = new JScrollPane(getList()) {
                {
                    setBorder(null);
                    getVerticalScrollBar().setFocusable(false);
                    getHorizontalScrollBar().setFocusable(false);
                }
            };
            setBorder(BorderFactory.createLineBorder(Color.BLACK));
            add(scroll);
        }
    };

    private final int rowCount = 10;

    protected final JList<String> list = new JList<String>() {
        {
            setFocusable(false);
            setRequestFocusEnabled(false);
        }
    };

    protected final JTextComponent textComponent;

    public AutoCompleter(final JTextComponent jtc) {
        this.textComponent = jtc;
        jtc.putClientProperty(COMPLETION, this);

        jtc.getDocument().addDocumentListener(documentListener);

        jtc.registerKeyboardAction(shift(-1),
                                   KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),
                                   JComponent.WHEN_FOCUSED);
        jtc.registerKeyboardAction(shift(1),
                                   KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),
                                   JComponent.WHEN_FOCUSED);
        jtc.registerKeyboardAction(shift(-(rowCount - 1)),
                                   KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0),
                                   JComponent.WHEN_FOCUSED);
        jtc.registerKeyboardAction(shift(rowCount - 1),
                                   KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0),
                                   JComponent.WHEN_FOCUSED);
        jtc.registerKeyboardAction(shift(Integer.MIN_VALUE),
                                   KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0),
                                   JComponent.WHEN_FOCUSED);
        jtc.registerKeyboardAction(shift(Integer.MAX_VALUE),
                                   KeyStroke.getKeyStroke(KeyEvent.VK_END, 0),
                                   JComponent.WHEN_FOCUSED);
        jtc.registerKeyboardAction(showAction,
                                   KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, KeyEvent.CTRL_DOWN_MASK),
                                   JComponent.WHEN_FOCUSED);
        jtc.registerKeyboardAction(hideAction,
                                   KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                                   JComponent.WHEN_FOCUSED);
    }

    /**
     * @return the list
     */
    public JList<String> getList() {
        return list;
    }

    /**
     * @return the popup
     */
    public JPopupMenu getPopup() {
        return popup;
    }

    private void showPopup() {
        getPopup().setVisible(false);
        if(textComponent.isEnabled() && updateListData() && getList().getModel().getSize() != 0) {
            textComponent.getDocument().addDocumentListener(documentListener);
            textComponent.registerKeyboardAction(acceptAction,
                                                 KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                                                 JComponent.WHEN_FOCUSED);
            int size = getList().getModel().getSize();
            getList().setVisibleRowCount(size < rowCount ? size : rowCount);

            int xPos = 0;
            try {
                int pos = Math.min(textComponent.getCaret().getDot(),
                                   textComponent.getCaret().getMark());
                xPos = textComponent.getUI().modelToView(textComponent, pos).x;
            } catch(BadLocationException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
            getPopup().show(textComponent, xPos, textComponent.getHeight());
        } else {
            getPopup().setVisible(false);
        }
        textComponent.requestFocus();
    }

    /**
     * user has selected some item in the list, update textfield accordingly
     * <p/>
     * @param selected
     */
    protected abstract void acceptedListItem(String selected);
    
    /**
     * Move the list selection within its boundaries
     * <p>
     * @param val
     */
    protected void shiftSelection(int val) {
        int si = getList().getSelectedIndex() + val;
        si = Math.min(Math.max(0, si), getList().getModel().getSize() - 1);
        getList().setSelectedIndex(si);
        getList().ensureIndexIsVisible(si);
    }
    
    
    /**
     * update list model depending on the data in textfield
     * <p/>
     * @return whether to display the list
     */
    protected abstract boolean updateListData();

}
