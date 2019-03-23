package com.mikyou.retrofit.api.generator.ui.adapter

import javax.swing.table.AbstractTableModel

class TableAdapter(columnNames: List<String>) : AbstractTableModel() {
    private val mColumnNames: List<String> = columnNames
    private val mDataList: MutableList<MutableList<String>> = mutableListOf()
    override fun getRowCount(): Int {
        return mDataList.size
    }

    override fun getColumnCount(): Int {
        return mColumnNames.size
    }

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        return mDataList[rowIndex][columnIndex]
    }

    override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean {
        return true
    }

    override fun getColumnClass(columnIndex: Int): Class<*> {
        return getValueAt(0, columnIndex).javaClass
    }

    override fun getColumnName(column: Int): String {
        return mColumnNames[column]
    }

    override fun setValueAt(aValue: Any?, rowIndex: Int, columnIndex: Int) {
        mDataList[rowIndex][columnIndex] = aValue as String
        fireTableCellUpdated(rowIndex, columnIndex)
    }

    fun addRowData(rowData: MutableList<String>): Int {
        val firstRow: Int = mDataList.size - 1
        mDataList.add(rowData)
        fireTableRowsInserted(firstRow, mDataList.size - 1)
        return mDataList.size - 1
    }

    fun deleteRowData(rowDataIndex: Int) {
        mDataList.removeAt(rowDataIndex)
        fireTableRowsDeleted(rowDataIndex, rowDataIndex)
    }
}