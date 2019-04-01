package com.mikyou.retrofit.api.generator.ui;

import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import com.mikyou.retrofit.api.generator.ext.ExtGUIKt;
import com.mikyou.retrofit.api.generator.helper.JsonParserHelper;
import com.mikyou.retrofit.api.generator.ui.adapter.TableAdapter;
import com.mikyou.retrofit.api.generator.ui.model.ViewDataGeneratorType;
import com.mikyou.retrofit.api.generator.ui.model.ViewDataParams;
import com.mikyou.retrofit.api.generator.ui.model.ViewDataResponse;
import com.mikyou.retrofit.api.generator.ui.model.ViewDataRetrofitApi;
import com.mikyou.retrofit.api.generator.ui.model.ViewDataRetrofitApiWrapper;
import com.mikyou.retrofit.api.generator.ui.model.ViewDataSupportLanguage;
import com.mikyou.retrofit.api.generator.ui.model.ViewDataSupportLibrary;

import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

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
	private JPanel mPanelRequestBody;
	private JButton mBtnHeaderParamDelete;
	private JButton mBtnHeaderParamAdd;
	private JButton mBtnQueryParamDelete;
	private JButton mBtnQueryParamAdd;
	private JButton mBtnFieldParamDelete;
	private JButton mBtnFieldParamAdd;
	private JButton mBtnRequestBodyDelete;
	private JButton mBtnRequestBodyAdd;
	private JTextField mTxtFieldMethodName;
	private JPanel mPanelMethodName;
	private JCheckBox mCheckBoxCustomMethod;
	private JTextField mTxtFieldInterface;
	private JLabel mLabelInterface;
	private JBTable mTableHeader;
	private JBTable mTableRequestBody;
	private JBTable mTableQueryParam;
	private JBTable mTableFieldParam;
	private TableAdapter mAdapterTableHeader;
	private TableAdapter mAdapterTableRequestBody;
	private TableAdapter mAdapterTableQueryParam;
	private TableAdapter mAdapterTableFieldParam;

	private static final String HTTP_GET = "GET";
	private static final String HTTP_POST = "POST";
	private static final String HTTP_PUT = "PUT";
	private static final String HTTP_DELETE = "DELETE";

	private static final String TAB_TITLE_QUERY_PARAMS = "Query Params";
	private static final String TAB_TITLE_FIELD_PARAMS = "Field Params";
	private static final String TAB_TITLE_REQUEST_BODY = "Request Body";

	private static final String LANGUAGE_KOTLIN = "Kotlin";
	private static final String LANGUAGE_JAVA = "Java";
	private static final String LIBRARY_RXJAVA = "RxJava";
	private static final String LIBRARY_COROUTINE = "Coroutine";

	private static final String PARAM = "[a-zA-Z][a-zA-Z0-9_-]*";
	private static final Pattern PARAM_URL_REGEX = Pattern.compile("\\{(" + PARAM + ")\\}");

	private List<ViewDataRetrofitApi> mViewDataRetrofitApiList = new ArrayList<>();
	private int[] mSelectedIndexArray;
	private boolean mIsGenerateInterfaceName = false;

	public RetrofitApiGeneratorDialog() {
		setContentPane(contentPane);
		setModal(true);
		getRootPane().setDefaultButton(mBtnGenerate);
	}

	public RetrofitApiGeneratorDialog(boolean isGenerateInterfaceName) {
		this();
		mIsGenerateInterfaceName = isGenerateInterfaceName;
		initViews();
		registerEvents();
	}

	private void initViews() {
		initInterfaceView();
		initFormDataRadioBtnGroup();
		initTabsPanel();
		initTables();
		refreshCBoxLibs(Objects.requireNonNull(mCBoxLanguage.getSelectedItem()).toString());
		mPanelMethodName.setVisible(false);
		mViewDataRetrofitApiList.clear();
	}

	private void initInterfaceView() {
		if (mIsGenerateInterfaceName) {
			mLabelInterface.setVisible(true);
			mTxtFieldInterface.setVisible(true);
		} else {
			mLabelInterface.setVisible(false);
			mTxtFieldInterface.setVisible(false);
		}
	}

	private void initTables() {
		//init headers params table
		mAdapterTableHeader = new TableAdapter(Arrays.asList("param name", "param type"));
		mTableHeader = new JBTable(mAdapterTableHeader);
		initTable(mTableHeader, mPanelHeaders);
		//init request body table
		mAdapterTableRequestBody = new TableAdapter(Arrays.asList("param name", "param type"));
		mTableRequestBody = new JBTable(mAdapterTableRequestBody);
		initTable(mTableRequestBody, mPanelRequestBody);
		//init query params table
		mAdapterTableQueryParam = new TableAdapter(Arrays.asList("param name", "param type"));
		mTableQueryParam = new JBTable(mAdapterTableQueryParam);
		initTable(mTableQueryParam, mPanelQueryParams);
		//init field params table
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

	private void initTabsPanel() {
		mTabsPanel.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		refreshTabsPanel();
	}

	private void initFormDataRadioBtnGroup() {
		ButtonGroup formDataRadioBtnGroup = new ButtonGroup();
		mRadioBtnFormData.setSelected(true);
		formDataRadioBtnGroup.add(mRadioBtnFormData);
		formDataRadioBtnGroup.add(mRadioBtnFormUrlEncoded);
	}

	private void registerEvents() {
		registerRequestMethodCBoxEvents();
		registerFormDataRadioBtnEvents();
		registerTableEvents();
		registerCheckBoxEvents();
		registerLanguageCBoxEvents();
		registerApiBtnEvents();
		registerJListEvents();
		registerGenerateEvents();
	}

	private void registerCheckBoxEvents() {
		mCheckBoxCustomMethod.addActionListener(actionEvent -> {
			mPanelMethodName.setVisible(mCheckBoxCustomMethod.isSelected());
		});
	}

	private void registerJListEvents() {
		mJListApi.addListSelectionListener(listSelectionEvent -> {
			mSelectedIndexArray = mJListApi.getSelectedIndices();
		});
	}

	private void registerApiBtnEvents() {
		mBtnAdd.addActionListener(actionEvent -> {
			addRetrofitApiItem();
			if (!mViewDataRetrofitApiList.isEmpty()) {
				mBtnDelete.setEnabled(true);
			}
		});

		mBtnDelete.addActionListener(actionEvent -> {
			if (mViewDataRetrofitApiList.isEmpty()) {
				mBtnDelete.setEnabled(false);
				return;
			}

			if (mSelectedIndexArray.length > 0 && mSelectedIndexArray.length <= mViewDataRetrofitApiList.size()) {
				for (int selectedIndex : mSelectedIndexArray) {
					if (selectedIndex >= 0 && selectedIndex <= mViewDataRetrofitApiList.size() - 1
							&& mViewDataRetrofitApiList.get(selectedIndex) != null) {
						mViewDataRetrofitApiList.remove(selectedIndex);
					}
				}
				refreshRetrofitApiListView();
			}
		});
	}

	private void refreshRetrofitApiListView() {
		mJListApi.setModel(new DefaultComboBoxModel(getRetrofitApiNameList()));
		mJListApi.setSelectedIndex(0);
	}

	private Object[] getRetrofitApiNameList() {
		return mViewDataRetrofitApiList.stream().map(it ->
				String.format("Request Method: %s,  Url: %s, Method: %s", it.getRequestMethod(), it.getRequestUrl(), it.getMethodName())
		).toArray();
	}

	//添加api item
	private void addRetrofitApiItem() {
		String requestMethod = mCBoxHttpMethod.getSelectedItem() == null ? HTTP_GET : mCBoxHttpMethod.getSelectedItem().toString();
		String requestUrl = mTxtFieldUrl.getText();
		boolean isFormUrlEncoded = mRadioBtnFormUrlEncoded.isSelected();
		List<ViewDataParams> headerParams = buildParamsFromTable(mAdapterTableHeader);
		List<ViewDataParams> requestBodyParams = buildParamsFromTable(mAdapterTableRequestBody);
		List<ViewDataParams> queryParams = buildParamsFromTable(mAdapterTableQueryParam);
		List<ViewDataParams> fieldParams = buildParamsFromTable(mAdapterTableFieldParam);
		Set<String> paths = getUrlPaths(requestUrl);
		ViewDataResponse response = new ViewDataResponse(mTxtFieldModelName.getText(), mRadioBtnIsList.isSelected(), mTxtAreaModelJson.getText(), new ArrayList<>());
		String methodName = getMethodName();
		ViewDataSupportLanguage supportLanguage = getSupportLanguage();
		ViewDataSupportLibrary supportLibrary = getSupportLibrary();

		ViewDataRetrofitApi retrofitApi = new ViewDataRetrofitApi(requestMethod, requestUrl, isFormUrlEncoded, headerParams,
				requestBodyParams, queryParams, fieldParams, paths, "", response, methodName, supportLanguage, supportLibrary);

		mViewDataRetrofitApiList.add(retrofitApi);
		refreshRetrofitApiListView();

		resetComponentsState();
	}

	private Set<String> getUrlPaths(String requestUrl) {
		Matcher m = PARAM_URL_REGEX.matcher(requestUrl);
		Set<String> patterns = new LinkedHashSet<>();
		while (m.find()) {
			patterns.add(m.group(1));
		}
		return patterns;
	}

	private String getMethodName() {
		if (!mTxtFieldMethodName.getText().isEmpty()) {
			return mTxtFieldMethodName.getText();
		}

		Object selectedItem = mCBoxHttpMethod.getSelectedItem();
		if (selectedItem == null) {
			return "";
		}

		String modelName = mRadioBtnIsList.isSelected() ? String.format("%sList", mTxtFieldModelName.getText()) : mTxtFieldModelName.getText();
		switch (selectedItem.toString()) {
			case HTTP_GET:
				return String.format("fetch%s", modelName);
			case HTTP_PUT:
				return String.format("update%s", modelName);
			case HTTP_DELETE:
				return String.format("delete%s", modelName);
			case HTTP_POST:
				return String.format("submit%s", modelName);
		}

		return "";
	}

	private ViewDataSupportLibrary getSupportLibrary() {
		Object selectedItem = mCBoxLibrary.getSelectedItem();
		if (selectedItem == null) {
			return ViewDataSupportLibrary.LIBRARY_RXJAVA;
		}

		if (selectedItem.toString().equals(LIBRARY_RXJAVA)) {
			return ViewDataSupportLibrary.LIBRARY_RXJAVA;
		}

		if (selectedItem.toString().equals(LIBRARY_COROUTINE)) {
			return ViewDataSupportLibrary.LIBRARY_COROUTINE;
		}

		return ViewDataSupportLibrary.LIBRARY_RXJAVA;
	}

	private ViewDataSupportLanguage getSupportLanguage() {
		Object selectedItem = mCBoxLanguage.getSelectedItem();
		if (selectedItem == null) {
			return ViewDataSupportLanguage.LANGUAGE_KOTLIN;
		}

		if (selectedItem.toString().equals(LANGUAGE_JAVA)) {
			return ViewDataSupportLanguage.LANGUAGE_JAVA;
		}

		if (selectedItem.toString().equals(LANGUAGE_KOTLIN)) {
			return ViewDataSupportLanguage.LANGUAGE_KOTLIN;
		}

		return ViewDataSupportLanguage.LANGUAGE_KOTLIN;
	}

	private List<ViewDataParams> buildParamsFromTable(TableAdapter tableAdapter) {

		List<ViewDataParams> dataParamsList = new ArrayList<>();
		for (int i = 0; i < tableAdapter.getRowCount(); i++) {
			String paramName = (String) tableAdapter.getValueAt(i, 0);
			String paramType = (String) tableAdapter.getValueAt(i, 1);
			dataParamsList.add(new ViewDataParams(paramName, paramType));
		}

		return dataParamsList;
	}

	private void resetComponentsState() {
		mCBoxHttpMethod.setSelectedIndex(0);
		mTxtFieldUrl.setText("");
		mRadioBtnFormData.setSelected(true);
		mRadioBtnFormUrlEncoded.setSelected(false);
		refreshTabsPanel();
		mAdapterTableHeader.clearAll();
		mAdapterTableFieldParam.clearAll();
		mAdapterTableQueryParam.clearAll();
		mAdapterTableRequestBody.clearAll();
		mTabsPanel.setSelectedIndex(0);
		mTxtFieldModelName.setText("");
		mTxtAreaModelJson.setText("");
		mRadioBtnIsList.setSelected(false);
		mCBoxLanguage.setSelectedIndex(0);
		mCBoxLibrary.setSelectedIndex(0);
	}

	private void registerRequestMethodCBoxEvents() {
		mCBoxHttpMethod.addItemListener(itemEvent -> refreshTabsPanel());
	}

	private void registerTableEvents() {
		//headers params btn add click events
		mBtnHeaderParamAdd.addActionListener(e -> {
			int addedIndex = mAdapterTableHeader.addRowData(Arrays.asList("", ""));
			mTableHeader.setRowSelectionInterval(addedIndex, addedIndex);
			mBtnHeaderParamDelete.setEnabled(true);
		});

		//headers params btn delete click events
		mBtnHeaderParamDelete.addActionListener(e -> {
			refreshRowSelection(mTableHeader, mAdapterTableHeader, mBtnHeaderParamDelete);
		});

		//request body btn add click events
		mBtnRequestBodyAdd.addActionListener(actionEvent -> {
			int addedIndex = mAdapterTableRequestBody.addRowData(Arrays.asList("", ""));
			mTableRequestBody.setRowSelectionInterval(addedIndex, addedIndex);
			mBtnRequestBodyDelete.setEnabled(true);
		});

		//request body btn delete click events
		mBtnRequestBodyDelete.addActionListener(actionEvent -> {
			refreshRowSelection(mTableRequestBody, mAdapterTableRequestBody, mBtnRequestBodyDelete);
		});
		//query params btn add click events
		mBtnQueryParamAdd.addActionListener(e -> {
			int addedIndex = mAdapterTableQueryParam.addRowData(Arrays.asList("", ""));
			mTableQueryParam.setRowSelectionInterval(addedIndex, addedIndex);
			mBtnQueryParamDelete.setEnabled(true);
		});

		//query params btn delete click events
		mBtnQueryParamDelete.addActionListener(e -> {
			refreshRowSelection(mTableQueryParam, mAdapterTableQueryParam, mBtnQueryParamDelete);
		});

		//field params btn add click events
		mBtnFieldParamAdd.addActionListener(e -> {
			int addedIndex = mAdapterTableFieldParam.addRowData(Arrays.asList("", ""));
			mTableFieldParam.setRowSelectionInterval(addedIndex, addedIndex);
			mBtnFieldParamDelete.setEnabled(true);
		});

		//field params btn delete click events
		mBtnFieldParamDelete.addActionListener(e -> {
			refreshRowSelection(mTableFieldParam, mAdapterTableFieldParam, mBtnFieldParamDelete);
		});
	}

	private void refreshRowSelection(JBTable table, TableAdapter adapter, JButton btnDelete) {
		int selectionRowIndex = table.getSelectedRow();
		if (selectionRowIndex > -1) {
			adapter.deleteRowData(selectionRowIndex);
		}
		if (table.getRowCount() <= 0) {
			btnDelete.setEnabled(false);
			return;
		}
		if (selectionRowIndex > 0 && selectionRowIndex < table.getRowCount()) {
			table.setRowSelectionInterval(selectionRowIndex - 1, selectionRowIndex - 1);
		} else if (selectionRowIndex == 0) {
			table.setRowSelectionInterval(0, 0);
		} else if (selectionRowIndex == table.getRowCount()) {
			table.setRowSelectionInterval(table.getRowCount() - 1, table.getRowCount() - 1);
		}

	}

	private void registerLanguageCBoxEvents() {
		mCBoxLanguage.addItemListener(e -> refreshCBoxLibs(e.getItem().toString()));
	}

	private void registerFormDataRadioBtnEvents() {
		mRadioBtnFormData.addActionListener(e -> {
			//选择form-data
			if (((JRadioButton) e.getSource()).isSelected()) {
				refreshTabsPanel();
			}
		});

		mRadioBtnFormUrlEncoded.addActionListener(e -> {
			//选择form-url-encoded-data
			if (((JRadioButton) e.getSource()).isSelected()) {
				refreshTabsPanel();
			}
		});
	}

	private void registerGenerateEvents() {
		mBtnGenerate.addActionListener(e -> {
			generateClicked();
			dispose();
		});

		mBtnCancel.addActionListener(e -> {
			cancelClicked();
			dispose();
		});

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				cancelClicked();
				dispose();
			}
		});

		contentPane.registerKeyboardAction(e -> {
			cancelClicked();
			dispose();
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	}

	private void refreshCBoxLibs(String language) {
		mCBoxLibrary.removeAllItems();
		mCBoxLibrary.addItem(LIBRARY_RXJAVA);
		if (language.equals(LANGUAGE_KOTLIN)) {
			mCBoxLibrary.addItem(LIBRARY_COROUTINE);
		}
	}

	private void refreshTabsPanel() {
		//如果是isFormUrlEncoded 全部采用@Field模式
		if (mRadioBtnFormUrlEncoded.isSelected()) {
			mTabsPanel.setEnabledAt(mTabsPanel.indexOfTab(TAB_TITLE_FIELD_PARAMS), true);
			mTabsPanel.setEnabledAt(mTabsPanel.indexOfTab(TAB_TITLE_QUERY_PARAMS), false);
			mTabsPanel.setEnabledAt(mTabsPanel.indexOfTab(TAB_TITLE_REQUEST_BODY), false);
			return;
		}

		Object selectedItem = mCBoxHttpMethod.getSelectedItem();
		if (selectedItem == null) {
			mTabsPanel.setEnabledAt(mTabsPanel.indexOfTab(TAB_TITLE_QUERY_PARAMS), true);
			mTabsPanel.setEnabledAt(mTabsPanel.indexOfTab(TAB_TITLE_REQUEST_BODY), false);
			mTabsPanel.setEnabledAt(mTabsPanel.indexOfTab(TAB_TITLE_FIELD_PARAMS), false);
			return;
		}

		//如果是PUT或者POST请求，采用@Body形式，不采用@Query形式
		if (selectedItem.toString().equals(HTTP_PUT) || selectedItem.toString().equals(HTTP_POST)) {
			mTabsPanel.setEnabledAt(mTabsPanel.indexOfTab(TAB_TITLE_REQUEST_BODY), true);
			mTabsPanel.setEnabledAt(mTabsPanel.indexOfTab(TAB_TITLE_QUERY_PARAMS), false);
		} else {//否则采用@Query形式
			mTabsPanel.setEnabledAt(mTabsPanel.indexOfTab(TAB_TITLE_QUERY_PARAMS), true);
			mTabsPanel.setEnabledAt(mTabsPanel.indexOfTab(TAB_TITLE_REQUEST_BODY), false);
		}

		mTabsPanel.setEnabledAt(mTabsPanel.indexOfTab(TAB_TITLE_FIELD_PARAMS), false);
	}

	private void generateClicked() {
		if (mApiGenerateCallback != null && !mViewDataRetrofitApiList.isEmpty()) {
			ViewDataRetrofitApi viewDataRetrofitApi = mViewDataRetrofitApiList.get(0);
			ViewDataSupportLanguage supportLanguage = viewDataRetrofitApi.getSupportLanguage();
			ViewDataSupportLibrary supportLibrary = viewDataRetrofitApi.getSupportLibrary();
			ViewDataGeneratorType generatorType = ViewDataGeneratorType.KOTLIN_RXJAVA;
			if (supportLanguage == ViewDataSupportLanguage.LANGUAGE_KOTLIN && supportLibrary == ViewDataSupportLibrary.LIBRARY_COROUTINE) {
				generatorType = ViewDataGeneratorType.KOTLIN_COROUTINE;
			} else if (supportLanguage == ViewDataSupportLanguage.LANGUAGE_JAVA) {
				generatorType = ViewDataGeneratorType.JAVA_RXJAVA;
			}

			mApiGenerateCallback.onGenerateClicked(new ViewDataRetrofitApiWrapper(mViewDataRetrofitApiList, mTxtFieldInterface.getText(), generatorType));
		}
	}

	private void cancelClicked() {
		if (mApiGenerateCallback != null) {
			mApiGenerateCallback.onCancelClicked();
		}
	}

	public static void main(String[] args) {
		RetrofitApiGeneratorDialog dialog = new RetrofitApiGeneratorDialog(true);
		dialog.setRetrofitApiGenerateCallback(new RetrofitApiGenerateCallback() {
			@Override
			public void onGenerateClicked(@Nonnull ViewDataRetrofitApiWrapper retrofitApisWrapper) {
				JsonParserHelper parserHelper = new JsonParserHelper();
				String json = retrofitApisWrapper.getViewDataRetrofitApis().get(0).getResponse().getModelJson();
				String modelName = retrofitApisWrapper.getViewDataRetrofitApis().get(0).getResponse().getModelName();
				parserHelper.parse(json, modelName, new JsonParserHelper.ParseListener() {
					@Override
					public void onParseComplete(String str) {
						System.out.println(str);
					}

					@Override
					public void onParseError(Exception e) {

					}
				});
			}

			@Override
			public void onCancelClicked() {

			}
		});
		ExtGUIKt.showDialog(dialog, 650, 700, true, false);
		System.exit(0);
	}

	private RetrofitApiGenerateCallback mApiGenerateCallback;

	public void setRetrofitApiGenerateCallback(RetrofitApiGenerateCallback callback) {
		mApiGenerateCallback = callback;
	}

	public interface RetrofitApiGenerateCallback {
		void onGenerateClicked(@Nonnull ViewDataRetrofitApiWrapper retrofitApisWrapper);

		void onCancelClicked();
	}
}


