//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// SystemFrame.java (sed.ui)
// Utworzono 2005-04-11 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import com.nestor.cs.Main;
import com.nestor.cs.Messages;
import com.nestor.cs.tasks.def.ConfigurableTask;
import com.nestor.cs.tasks.def.DiagramComponent;
import com.nestor.cs.ui.actions.About;
import com.nestor.cs.ui.actions.ExitSystem;
import com.nestor.cs.ui.actions.SharedActionRegistry;
import com.nestor.cs.ui.document.DiagramDocument;
import com.nestor.cs.ui.document.DiagramFrame;
import com.nestor.cs.ui.document.TaskDialog;
import com.nestor.cs.ui.document.TaskFrame;
import com.nestor.cs.ui.event.ActiveDocumentChangedEvent;
import com.nestor.cs.ui.event.ActiveDocumentChangedListener;
import com.nestor.cs.ui.event.DiagramDocumentChangeEvent;
import com.nestor.cs.ui.event.DiagramDocumentChangeListener;
import com.nestor.cs.ui.event.ObjectSelectionEvent;
import com.nestor.cs.ui.event.ObjectSelectionListener;
import com.nestor.cs.ui.paramedit.ParameterEdit;
import com.nestor.cs.util.event.StateEvent;
import com.nestor.cs.util.event.StateListener;
import com.nestor.cs.util.proc.Task;
import com.nestor.cs.util.swing.ExtJMenuItem;
import com.nestor.cs.util.util.DoubleMap;
import com.nestor.cs.util.util.GraphicsTools;
import com.nestor.cs.util.util.param.ParameterList;

public class SystemFrame extends JFrame implements
        ActiveDocumentChangedListener, ObjectSelectionListener,
        DiagramDocumentChangeListener {

    private class DocStateListener implements StateListener {

        public void stateChanged(StateEvent e) {
            if (e.getNewState() == Task.RUNNING) {
                SystemFrame.this.docsRunning++;
                if (SystemFrame.this.showOutputFrame)
                    OutputFrame.startFrame();
            } else {
                if (e.getOldState() == Task.RUNNING)
                    SystemFrame.this.docsRunning--;
            }
        }
    }

    private class FrameListener extends WindowAdapter implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof JMenuItem) {
                JMenuItem jcb = (JMenuItem) e.getSource();
                if (jcb == SystemFrame.this.jmiCloseAll) {
                    closeAllWindows();
                } else {
                    JFrame f = (JFrame) SystemFrame.this.componentFrames.getEndMap().get(jcb);
                    if (f != null)
                        f.toFront();
                }
            }
        }

        @Override
        public void windowClosing(WindowEvent e) {
            JMenuItem jcb = (JMenuItem) SystemFrame.this.componentFrames.getFrontMap().get(
                    e.getWindow());
            if (jcb == null)
                return;
            SystemFrame.this.componentFrames.getFrontMap().remove(e.getWindow());
            SystemFrame.this.jmWindows.remove(jcb);
        }

        @Override
        public void windowOpened(WindowEvent e) {
            if (e.getWindow() instanceof JFrame) {
                JFrame f = (JFrame) e.getWindow();
                JMenuItem jcb = new JMenuItem(f.getTitle());
                jcb.addActionListener(SystemFrame.this.sharedListener);
                SystemFrame.this.componentFrames.getFrontMap().put(f, jcb);
                SystemFrame.this.jmWindows.add(jcb);
            }
        }

    }

    private static SystemFrame instance;

    public static void createFrame() {
        new SystemFrame();
    }

    public static SystemFrame getInstance() {
        return instance;
    }

    // na pierwszej stronie mapy:
    // kluczem jest JFrame a warto�ci� odpowiadaj�cy
    // JMenuItem
    public DoubleMap componentFrames = new DoubleMap();

    private Object currentlySelectedObject = null;

    private HashMap diagramComponentDialogs = new HashMap();

    private HashMap diagramComponentFrames = new HashMap();

    private int docsRunning = 0;

    private DocStateListener docStateListener = new DocStateListener();

    public DocumentElementFactoryTree elementTree = null;

    private JMenuItem jmiCloseAll;

    private JMenu jmWindows = new JMenu(Messages
            .getString("SystemFrame.windows")); //$NON-NLS-1$

    protected JPanel jpCenter;

    private JSplitPane jsplCenterPane;

    private JSplitPane jsplLeftPane;

    private JSplitPane jsplMainPane;

    public MainDesktop mainDesktop;

    public MessageArea messageArea = null;

    public ParameterEdit parameterEdit = null;

    private FrameListener sharedListener = new FrameListener();

    private boolean showOutputFrame = false;

    public CustomToolBar toolBar = null;

    public SystemFrame() throws HeadlessException {
        super(Messages.getString("SystemFrame.sed")); //$NON-NLS-1$

        URL url = this.getClass().getResource("images/sed16.gif"); //$NON-NLS-1$
        ImageIcon ic = new ImageIcon(url);
        ImageIcon ic2 = GraphicsTools.makeTransparentIcon(ic, Color.CYAN);
        setIconImage(ic2.getImage());

        instance = this;
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                ActionEvent ae = new ActionEvent(SystemFrame.this, 0, null);
                ActionListener al;
                try {
                    al = SharedActionRegistry
                            .getActionSharedInstance(ExitSystem.class);
                    al.actionPerformed(ae);
                } catch (ClassCastException e) {
                }
            }
        });
        initComponents();

        setSize(800, 550);
        setExtendedState(Frame.MAXIMIZED_BOTH);
        setJMenuBar(new CustomMenuBar());

        getJMenuBar().add(this.jmWindows);
        this.jmiCloseAll = new JMenuItem(Messages.getString("SystemFrame.closeAll")); //$NON-NLS-1$
        this.jmiCloseAll.addActionListener(this.sharedListener);
        this.jmWindows.add(this.jmiCloseAll);
        this.jmWindows.addSeparator();

        JMenu jmAbout = new JMenu(Messages.getString("SystemFrame.other")); //$NON-NLS-1$
        getJMenuBar().add(jmAbout);
        ExtJMenuItem jmiAbout = new ExtJMenuItem(SharedActionRegistry
                .getActionSharedInstance(About.class));
        jmAbout.add(jmiAbout);

        setVisible(true);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                restoreCenterSplitPane();
                restoreLeftSplitPane();
                restoreRightSplitPane();
                Main.pleaseWait.runme = false;
            }
        });
    }

    public void activeDocumentChanged(ActiveDocumentChangedEvent event) {
        setObjectSelected(null);
    }

    public void closeAllWindows() {
        JFrame[] fs = new JFrame[this.componentFrames.getFrontMap().size()];
        Iterator i = this.componentFrames.getFrontMap().keySet().iterator();
        int k = 0;
        while (i.hasNext()) {
            fs[k++] = (JFrame) i.next();
        }
        for (int j = 0; j < fs.length; j++) {
            fs[j].dispatchEvent(new WindowEvent(fs[j],
                    WindowEvent.WINDOW_CLOSING));
            fs[j].dispose();
        }
    }

    public TaskDialog createComponentDialog(DiagramComponent dc) {
        TaskDialog dlg = null;
        if (this.diagramComponentDialogs.containsKey(dc)) {
            Object v = this.diagramComponentDialogs.get(dc);
            if (v instanceof TaskDialog) {
                dlg = (TaskDialog) v;
                dlg.reinitViewer(dc);
            }
        }
        if (dlg == null) {
            dlg = new TaskDialog(dc);
            this.diagramComponentDialogs.put(dc, dlg);
            dlg.onCloseRemoveDCFromMap(this.diagramComponentDialogs);
            dlg.setModal(dc.doModalDialog());
            dlg.setVisible(true);
        }
        return dlg;
    }

    public TaskFrame createComponentFrame(DiagramComponent dc) {
        TaskFrame f = null;
        if (this.diagramComponentFrames.containsKey(dc)) {
            Object v = this.diagramComponentFrames.get(dc);
            if (v instanceof TaskFrame) {
                f = (TaskFrame) v;
                f.reinitViewer(dc);
                JMenuItem jcb = (JMenuItem) this.componentFrames.getFrontMap()
                        .get(f);
                if (jcb != null)
                    jcb.setText(f.getTitle());
            }
        }
        if (f == null) {
            f = new TaskFrame(dc);
            this.diagramComponentFrames.put(dc, f);
            f.onCloseRemoveDCFromMap(this.diagramComponentFrames);
            f.addWindowListener(this.sharedListener);
            f.setVisible(true);
        }
        return f;
    }

    public void documentChanged(DiagramDocumentChangeEvent event) {
        if (event.getChange() == DiagramDocumentChangeEvent.CHANGE_ELEMENT_REMOVED) {
            if (getObjectSelected() == event.getDiagramElement().getTask())
                setObjectSelected(null);
        }
    }

    public int getDocsRunning() {
        return this.docsRunning;
    }

    protected Object getObjectSelected() {
        return this.currentlySelectedObject;
    }

    public boolean getShowOutputFrame() {
        return this.showOutputFrame;
    }

    public void hideCenterSplitPane() {
        this.jsplMainPane.setDividerLocation(0.0f);
        this.jsplMainPane.setResizeWeight(0.0f);
    }

    private void initComponents() {
        this.jpCenter = new JPanel(new BorderLayout());
        this.mainDesktop = new MainDesktop();
        this.mainDesktop.addActiveDocumentChangedListener(this);
        this.messageArea = new MessageArea();
        this.messageArea.setVisible(false);

        this.jsplLeftPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false);
        this.jsplCenterPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false);
        this.jsplMainPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false);

        this.elementTree = new DocumentElementFactoryTree();
        this.elementTree.setVisible(false);
        this.parameterEdit = new ParameterEdit();
        this.parameterEdit.setVisible(false);
        this.jsplLeftPane.add(this.elementTree, JSplitPane.TOP);
        this.jsplLeftPane.add(this.parameterEdit, JSplitPane.BOTTOM);

        this.jsplLeftPane.setDividerSize(5);

        this.toolBar = new CustomToolBar();
        this.toolBar.setVisible(false);
        this.jpCenter.add(this.toolBar, BorderLayout.NORTH);

        this.jpCenter.add(this.mainDesktop, BorderLayout.CENTER);

        this.jsplCenterPane.add(this.jpCenter, JSplitPane.TOP);
        this.jsplCenterPane.add(this.messageArea, JSplitPane.BOTTOM);
        this.jsplCenterPane.setDividerSize(5);

        this.jsplMainPane.add(this.jsplLeftPane, JSplitPane.LEFT);
        this.jsplMainPane.add(this.jsplCenterPane, JSplitPane.RIGHT);
        this.jsplMainPane.setDividerSize(5);

        setContentPane(this.jsplMainPane);
    }

    public void invalidateLeftSplitPane() {
        this.jsplLeftPane.revalidate();
    }

    public void objectSelected(ObjectSelectionEvent e) {
        setObjectSelected(e.getSelectedObject());
    }

    public void restoreCenterSplitPane() {
        this.jsplMainPane.setDividerLocation(0.33f);
        this.jsplMainPane.setResizeWeight(0.33f);
    }

    public void restoreLeftSplitPane() {
        this.jsplLeftPane.setDividerLocation(0.7f);
        this.jsplLeftPane.setResizeWeight(0.7f);
    }

    public void restoreRightSplitPane() {
        this.jsplCenterPane.setDividerLocation(0.83f);
        this.jsplCenterPane.setResizeWeight(0.83f);
    }

    protected void setObjectSelected(Object obj) {
        if (obj == this.currentlySelectedObject)
            return;
        this.currentlySelectedObject = obj;
        if (this.parameterEdit == null)
            return;
        if (obj == null) {
            this.parameterEdit.showParameterList(null);
            return;
        }
        if (obj instanceof ConfigurableTask) {
            ConfigurableTask dp = (ConfigurableTask) obj;
            this.messageArea.showTemporaryLegend(Messages.getString("SystemFrame.0") //$NON-NLS-1$
                    + "\n" //$NON-NLS-1$
                    + Messages.getString("SystemFrame.2") //$NON-NLS-1$
                    + "\n" //$NON-NLS-1$
                    + Messages.getString("SystemFrame.4") //$NON-NLS-1$
                    + "\n" //$NON-NLS-1$
                    + Messages.getString("SystemFrame.4b")); //$NON-NLS-1$
            this.parameterEdit.showParameterList(new ParameterList(dp
                    .getConfigurationArguments()));
        } else {
            this.parameterEdit.showParameterList(null);
        }
    }

    public void setShowOutputFrame(boolean showOutputFrame) {
        this.showOutputFrame = showOutputFrame;
        if (this.docsRunning > 0)
            if (showOutputFrame) {
                OutputFrame.startFrame();
            } else {
                OutputFrame.closeFrame();
            }
    }

    public DiagramFrame showDocument(DiagramDocument doc) {
        doc.addDiagramDocumentChangeListener(this);
        doc.addStateListener(this.docStateListener);
        DiagramFrame df = new DiagramFrame(doc);
        df.getDiagramPanel().addObjectSelectionListener(this);
        this.mainDesktop.addDocumentFrame(df);
        return df;
    }

}