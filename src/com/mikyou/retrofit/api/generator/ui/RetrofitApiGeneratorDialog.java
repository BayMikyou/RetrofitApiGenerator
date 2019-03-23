package com.mikyou.retrofit.api.generator.ui;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import com.mikyou.retrofit.api.generator.ext.ExtGUIKt;
import com.mikyou.retrofit.api.generator.ui.adapter.TableAdapter;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Objects;

public class RetrofitApiGeneratorDialog extends JDialog {
    private JPanel contentPane;
    private JButton mBtnGenerate;
    private JButton mBtnCancel;
    private JComboBox mCBoxHttpMethod;
    private JTextField mTxtFieldUrl;
    private JTabbedPane mTabsPanel;
    private JTextField mTxtFieldModelName;
    private JTextArea mTxtAreaModelJson;
    private JRadioButton mRadioBtnIsList;
    private JList mJListApi;
    private JRadioButton mRadioBtnFormData;
    private JRadioButton mRadioBtnFormUrlEncoded;
    private JButton mBtnDelete;
    private JButton mBtnAdd;
    private JComboBox mCBoxLanguage;
    private JComboBox mCBoxLibrary;
    private JPanel mPanelQueryParams;
    private JPanel mPanelFieldParams;
    private JPanel mPanelHeaders;
    private JPanel mPanelResponse;
    private JButton mBtnHeaderParamDelete;
    private JButton mBtnHeaderParamAdd;
    private JButton mBtnQueryParamDelete;
    private JButton mBtnQueryParamAdd;
    private JButton mBtnFieldParamDelete;
    private JButton mBtnFieldParamAdd;
    private JBTable mTableHeader;
    private JBTable mTableQueryParam;
    private JBTable mTableFieldParam;
    private TableAdapter mAdapterTableHeader;
    private TableAdapter mAdapterTableQueryParam;
    private TableAdapter mAdapterTableFieldParam;
    public static final String HTTP_GET = "GET";
    public static final String HTTP_POST = "POST";
    public static final String HTTP_PUT = "PUT";
    public static final String HTTP_DELETE = "DELETE";

    private static final String TAB_TITLE_QUERY_PARAMS = "Query Params";
    private static final String TAB_TITLE_FIELD_PARAMS = "Field Params";

    private static final String CBOX_KOTLIN_LANGUAGE = "Kotlin";
    private static final String CBOX_JAVA_LANGUAGE = "Java";


    public RetrofitApiGeneratorDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(mBtnGenerate);
        initViews();
        registerEvents();
    }

    private void initViews() {
        initFormDataRadioBtnGroup();
        initTabsPanel();
        initTables();
        refreshCBoxLibs(Objects.requireNonNull(mCBoxLanguage.getSelectedItem()).toString());
    }

    private void initTables() {
        mAdapterTableHeader = new TableAdapter(Arrays.asList("param name", "param type"));
        mTableHeader = new JBTable(mAdapterTableHeader);
        initTable(mTableHeader, mPanelHeaders);

        mAdapterTableQueryParam = new TableAdapter(Arrays.asList("param name", "param type"));
        mTableQueryParam = new JBTable(mAdapterTableQueryParam);
        initTable(mTableQueryParam, mPanelQueryParams);

        mAdapterTableFieldParam = new TableAdapter(Arrays.asList("param name", "param type"));
        mTableFieldParam = new JBTable(mAdapterTableFieldParam);
        initTable(mTableFieldParam, mPanelFieldParams);
    }

    private void initTable(JBTable table, JPanel panel) {
        //设置顶部table header 文字居中
        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.setRowHeight(22);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JBScrollPane jScrollPane = new JBScrollPane(table);
        jScrollPane.createVerticalScrollBar();
        table.setFillsViewportHeight(true);
        panel.add(jScrollPane);
    }

    private void setupParamTypeColumn(TableColumn column) {
        JComboBox comboBox = new JComboBox();
        comboBox.addItem("String");
        comboBox.addItem("Int");
        comboBox.addItem("Double");
        comboBox.addItem("Float");
        comboBox.addItem("Byte");
        comboBox.setEditable(true);
        column.setCellEditor(new DefaultCellEditor(comboBox));
    }

    private void initTabsPanel() {
        mTabsPanel.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        mTabsPanel.setEnabledAt(mTabsPanel.indexOfTab(TAB_TITLE_QUERY_PARAMS), true);
        mTabsPanel.setEnabledAt(mTabsPanel.indexOfTab(TAB_TITLE_FIELD_PARAMS), false);
    }

    private void initFormDataRadioBtnGroup() {
        ButtonGroup formDataRadioBtnGroup = new ButtonGroup();
        mRadioBtnFormData.setSelected(true);
        formDataRadioBtnGroup.add(mRadioBtnFormData);
        formDataRadioBtnGroup.add(mRadioBtnFormUrlEncoded);
    }

    private void registerEvents() {
        registerFormDataRadioBtnEvents();
        registerTableEvents();
        registerCBoxEvents();
        registerGenerateEvents();
    }

    private void registerTableEvents() {
        mBtnHeaderParamAdd.addActionListener(e -> {
            int addedIndex = mAdapterTableHeader.addRowData(Arrays.asList("", ""));
            setupParamTypeColumn(mTableHeader.getColumnModel().getColumn(1));
            mTableHeader.setRowSelectionInterval(addedIndex, addedIndex);
        });
        mBtnHeaderParamDelete.addActionListener(e -> {
            refreshRowSelection(mTableHeader, mAdapterTableHeader);
            refreshDeleteBtn(mAdapterTableHeader, mBtnHeaderParamDelete);
        });

        mBtnQueryParamAdd.addActionListener(e -> {
            int addedIndex = mAdapterTableQueryParam.addRowData(Arrays.asList("", ""));
            setupParamTypeColumn(mTableQueryParam.getColumnModel().getColumn(1));
            mTableQueryParam.setRowSelectionInterval(addedIndex, addedIndex);
        });
        mBtnQueryParamDelete.addActionListener(e -> {
            refreshRowSelection(mTableQueryParam, mAdapterTableQueryParam);
            refreshDeleteBtn(mAdapterTableQueryParam, mBtnQueryParamDelete);
        });

        mBtnFieldParamAdd.addActionListener(e -> {
            int addedIndex = mAdapterTableFieldParam.addRowData(Arrays.asList("", ""));
            setupParamTypeColumn(mTableFieldParam.getColumnModel().getColumn(1));
            mTableFieldParam.setRowSelectionInterval(addedIndex, addedIndex);
        });
        mBtnFieldParamDelete.addActionListener(e -> {
            refreshRowSelection(mTableFieldParam, mAdapterTableFieldParam);
            refreshDeleteBtn(mAdapterTableFieldParam, mBtnFieldParamDelete);
        });
    }

    private void refreshRowSelection(JBTable table, TableAdapter adapter) {
        int selectionRowIndex = table.getSelectedRow();
        adapter.deleteRowData(selectionRowIndex);
        if (selectionRowIndex > 0 && selectionRowIndex < table.getRowCount()) {
            table.setRowSelectionInterval(selectionRowIndex - 1, selectionRowIndex - 1);
        } else if (selectionRowIndex == 0) {
            table.setRowSelectionInterval(0, 0);
        } else if (selectionRowIndex == table.getRowCount()) {
            table.setRowSelectionInterval(table.getRowCount() - 1, table.getRowCount() - 1);
        }
    }

    private void refreshDeleteBtn(TableAdapter adapter, JButton btn) {
        if (adapter.getColumnCount() == 0) {
            btn.setEnabled(false);
        }
    }

    private void registerCBoxEvents() {
        mCBoxLanguage.addItemListener(e -> refreshCBoxLibs(e.getItem().toString()));
    }

    private void registerFormDataRadioBtnEvents() {
        mRadioBtnFormData.addActionListener(e -> {
            if (((JRadioButton) e.getSource()).isSelected()) {
                //选择form-data
                mTabsPanel.setEnabledAt(mTabsPanel.indexOfTab(TAB_TITLE_QUERY_PARAMS), true);
                mTabsPanel.setEnabledAt(mTabsPanel.indexOfTab(TAB_TITLE_FIELD_PARAMS), false);
            }
        });

        mRadioBtnFormUrlEncoded.addActionListener(e -> {
            if (((JRadioButton) e.getSource()).isSelected()) {
                //选择form-url-encoded-data
                mTabsPanel.setEnabledAt(mTabsPanel.indexOfTab(TAB_TITLE_QUERY_PARAMS), false);
                mTabsPanel.setEnabledAt(mTabsPanel.indexOfTab(TAB_TITLE_FIELD_PARAMS), true);
            }
        });
    }

    private void registerGenerateEvents() {
        mBtnGenerate.addActionListener(e -> {
            onGenerateClicked();
            dispose();
        });

        mBtnCancel.addActionListener(e -> {
            onCancelClicked();
            dispose();
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancelClicked();
                dispose();
            }
        });

        contentPane.registerKeyboardAction(e -> {
            onCancelClicked();
            dispose();
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void refreshCBoxLibs(String language) {
        mCBoxLibrary.removeAllItems();
        mCBoxLibrary.addItem("RxJava");
        if (language.equals(CBOX_KOTLIN_LANGUAGE)) {
            mCBoxLibrary.addItem("Coroutine");
        }
    }

    private void onGenerateClicked() {

    }

    private void onCancelClicked() {

    }

    public static void main(String[] args) {
        RetrofitApiGeneratorDialog dialog = new RetrofitApiGeneratorDialog();
        ExtGUIKt.showDialog(dialog, 650, 700, true, false);
        System.exit(0);
    }
}
