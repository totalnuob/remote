
export class InputFilesInfoLookupNBReport{

    public static SCHEDULE_INVESTMENTS_DESCRIPTION =
        "1. Top left corner must contain the following headings: <br/>" +
        "<b>Tarragaon Master Fund LP</b><br/>" +
        "<b>Schedule of Investments - Tranche A</b> or <b>Schedule of Investments - Tranche B</b> (depending on tranche)<br/>" +
        "<b>Report date</b>, e.g. 'Июнь 30, 2017'" +
        "<br/>" +
        "<br/>" +
        "2. Tranche A records must be on the first sheet of Excel file, Tranche B records must be on the second sheet. " +
        "<br/>" +
        "<br/>" +
        "3. Schedule of Investments data must be presented in a table with the following header columns:<br/>" +
        "| &nbsp;  | <b>Imvestment</b> | <b>Capital Commitments</b> | <b>Net Cost</b> | <b>Fair Value |</b> <br/>" +
        "first header column must be empty." +
        "<br/>" +
        "<br/>" +
        "4.  All records must be within the main header (Fund Investments, Co-Investments, etc) and the strategy header, e.g. <br/>" +
        "<b>Fund Investments</b> <small><i>// main header</i></small><br/> " +
        "<b>Specal Situations</b> <small><i>// strategy</i></small><br/>" +
        "Fund ABC <small><i>// investment </i></small><br/>" +
        "Fund XYZ <small><i>// investment</i></small>" +
        "<br/>" +
        "<br/>" +
        "5.Each header (main and strategy) must have closing Total Value record, e.g. 'Fund Investments' header must be followed by " +
        "some records, and a final 'Total Fund Investments' record that sums up the records of this header. <br/>" +
        "Total value record must start with 'Total' and contain the exact header name, " +
        "e.g. for Co-Investments, record 'Vertiv JV Holdings, LLC' must be followed by 'Total Vertiv JV Holdings, LLC', " +
        " not just 'Total Vertiv' or other." +
        "<br/>" +
        "<br/>" +
        "6. If there are two types of main headers, 'Fund Investments' and 'Co-Investments', then <br/>" +
        "the list must end with 'Total Fund Investments and Co-Investments' record that sums up all the records." +
        "";

    public static STATEMENT_ASSETS_LIABILITIES_PX_DESCRIPTION =
        "1. Tranche A records must be on the first sheet of Excel file, Tranche B records must be on the second sheet. " +
        "<br/>" +
        "<br/>" +
        "2. Tranche A data must be presented in a table with the following header columns (two rows): <br/>" +
        "<u>row 1:</u> <small>| Tarragon Master Fund LP | </small><br/>" +
        "<u>row 2:</u> <small>| Total* | Tarragon GP LLC' Share | NICK Master Fund Ltd.'s Share | Tarragon LP | NICK Master Fund Lts.'s Share of Total |" +
        "Consolidation Adjustments | NICK Master Fund Ltd.'s Share of Tranche A Consolidated | </small>"  +
        "<br/>" +
        "<br/>" +
        "3. Tranche B data must be presented in a table with the following header columns: <br/>" +
        "<small>| Tarragon Master Fund LP| Tarragon LP | Total | Consolidation Adjustments | Tranche B Consolidated | </small>"  +
        "<br/>" +
        "<br/>" +
        "4. Data columns must be separated with an empty column. " +
        "<br/>" +
        "<br/>" +
        "5. All the records (both Tranche A and Tranche B) must be within a main header: <br/>" +
        "<b>Consolidated Statement of Assets, Liabilities and Partners' Capital</b> (balance records) " +
        "or <b>Consolidated Statement of Operations</b> (operations records)." +
        "<br/>" +
        "<br/>" +
        "6. Each record must have 1 or more headers, e.g. 'Assets', 'Liabilities', etc.<br/>" +
        "There is a predefined list of allowed types/headers. Every type/header from excel file must be stored in the list(database) beforehand." +
        "<br/>" +
        "<br/>" +
        "7. Each header must have closing <u>total value record</u> that sums up the records of this header." +
        "Total value record must start with 'Total' or 'Net' and contain the exact header name, " +
        "e.g. header 'Investment income (loss)' must be followed by 'Net investment income (loss)'. " +
        "<br/>" +
        "<br/>" +
        "8. Operations records, i.e. header <b>Consolidated Statement of Operations</b>, must end with total value record<br/>" +
        "<b>Net increase (decrease) in partners' capital resulting from operations</b>." +
        "<br/>" +
        "<br/>" +
        "9. <b>Partners' Capital</b> header can be skipped when there is only one line (exactly 'Partners' Capital)of this type." +
        " The record will be assigned <b>Partners' Capital</b> type onetheless." +
        "<br/>" +
        "<br/>" +
        "";

    public static STATEMENT_CASH_FLOWS_DESCRIPTION =
        "1. Top left corner has to contain the following headings: <br/>" +
        "<b>Tarragaon LP</b><br/>" +
        "<b>Consolidated Statement of Cash Flows for NICK Master Fund Ltd.</b>" +
        "<br/>" +
        "<br/>" +
        "2. Cash flows must be presented in a table with 4 data columns. Data columns must be separated with " +
        "empty columns, making it total 7 columns.<br/>" +
        "The first data column contains the names and headers/types of the reocrds.<br/>" +
        "The second data column contains tranche A cash flow amounts." +
        "The third data column contains tranche B cash flows amounts." +
        "The fourth data column contains total cash flows amounts for tranche A and B." +
        "<br/>" +
        "<br/>" +
        "3. Each record name must have bigger indentation than its header name. <br/>" +
        "Each sub-header must have bigger indentation than its parent header. E.g.:<br/>" +
        "<b>Cash flows from operating activities</b><br/>" +
        "&nbsp; &nbsp; Change in assets and liabilities:<br/>" +
        "&nbsp; &nbsp; &nbsp; &nbsp; Prepaid expenses" +
        "<br/>" +
        "<br/>" +
        "4. Special records: <br/>" +
        "<b>Net increase (decrease) in cash and cash equivalents</b> - sums all previous records.<br/>" +
        "<b>Cash and cash equivalents - beginning of period</b> - same value for all cash flows of the corresponding year<br/>" +
        "<b>Cash and cash equivalents - end of period</b> - sums all the precious records and 'Cash and cash equivalents - beginning of period'" +
        "";

    public static STATEMENT_CHANGES_DESCRIPTION =
        "1. Top left corner must contain the following headings: <br/>" +
        "<b>Tarragaon LP</b><br/>" +
        "<b>Consolidated Statement of Changes in Partner’s Capital for NICK Master Fund Ltd.</b><br/>" +
        "<b>Report date</b>, e.g. 'March 31, 2017'" +
        "<br/>" +
        "<br/>" +
        "2. Statement of Changes in Partners' Capital data must be presented in a table with variable number of columns and three data rows.<br/>" +
        "The first data row name must be 'Tranche A, the seond data row - 'Tranche B', the third - 'Total'." +
        "<br/>" +
        "<br/>" +
        "3. Data columns can have empty columns between them, but no more than consecutive three empty columns." +
        "<br/>" +
        "<br/>" +
        "";

    public static GENERAL_LEDGER_BALANCE_DESCRIPTION =
        "1. Singularity General Ledger Balance data must be presented in a table with the following header columns:<br/>" +
        "<small>| Acronym | Balance Date | Financial Statement Category | GL Account |	Financial Statement Category Description | " +
        "Chart of Accounts Description | Chart of Accounts Long Description | Seg Val1 | Seg Val2 | Seg Val3 | Seg Val4 | " +
        "GL Account Balance | Seg Val CCY | Fund CCY |</small>" +
        "<br/>" +
        "<br/>" +
        "2. Allowed Values: <br/>" +
        "<b>Acronym</b> - 'SINGULAR' or 'SINGULAR B', i.e. Tranche A and Tranche B respectively.<br/>" +
        "<b>Balance Date</b> - date in DD.MM.YYYY fornat<br/>" +
        "<b>Financial Statement Category</b> - 'A' (Assets), 'L' (Liabilities), 'E' (Equity), 'I' (Income), 'X' (Expenses)<br/>" +
        "<b>GL Account</b> - Account, e.g. '1010-0000-150-010'<br/>" +
        "<b>Financial Statement Category Description</b> - 'Cash and Cash Equivalents', etc.<br/>" +
        "<b>Chart of Accounts Description</b> - 'Cash - DDA', etc.<br/>" +
        "<b>Chart of Accounts Long Description</b> - longer description, contains more details<br/>" +
        "<b>Seg Val1, Seg Val2, Seg Val3, Seg Val4</b> - parts of GL Account (not stored)<br/>" +
        "<b>GL Account Balance</b> - account balance amount, numeric decimal value<br/>" +
        "<b>Seg Val CCY</b> - currency, e.g. 'USD'.<br/>" +
        "<b>Fund CCY</b> - fund currency, e.g. 'USD', 'EUR', etc." +
        "<br/>" +
        "<br/>" +
        "";

    public static NOAL_DESCRIPTION =
        "1. Net Other Assets and Liabilities (NOAL) data must be presented in a table with the following header columns:<br/>" +
        "<small>| <b>Date</b> | Transaction| <b>Investor Account/Portfolio Fund</b> | <b>Effective Date</b> | (Transaction) <b>Amount</b> | " +
        "(Transactin) <b>CCY</b> | (Functional) <b>Amount</b> | (Functional) <b>CCY</b> |</small><br/>" +
        "<br/>" +
        "<br/>" +
        "2. Allowed Values: <br/>" +
        "<b>Date</b> - date in DD.MM.YYYY fornat<br/>" +
        "<b>Transaction</b> - Transaction name or 'Ending Balance'.<br/>" +
        "<b>Investor Account/Portfolio Fund</b> - Investor Account or Fund name.<br/>" +
        "<b>Effective Date</b> - effective date in DD.MM.YYYY fornat<br/>" +
        "<b>Transaction Amount</b> - transaction amount, numeric decimal value <br/>" +
        "<b>Transaction CCY</b> - 'USD', 'EUR', etc.<br/>" +
        "<b>Functional Amount</b> - functional amount, numeric decimal value <br/>" +
        "<b>Functional CCY</b> - 'USD', 'EUR', etc." +
        "<br/>" +
        "<br/>" +
        "3. Must end with <b>REPORT TOTAL</b> record containing amount and currency in 'Functional Amount' and 'Functional CCY' respectively.<br/" +
        "<br/>" +
        "<br/>" +
        "";
}