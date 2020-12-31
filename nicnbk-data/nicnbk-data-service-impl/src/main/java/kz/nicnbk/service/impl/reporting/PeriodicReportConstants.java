package kz.nicnbk.service.impl.reporting;

/**
 * Created by magzumov on 18.01.2018.
 */
public final class PeriodicReportConstants {



    // TODO: RU_ prefix for russian
    // TODO: EN_
    /* PARSE CONSTANTS ************************************************************************************************/

    /*SOI REPORT */
    public static final String PARSE_SOI_REPORT_HEADER_SECURITY_NO = "Security No.";
    public static final String PARSE_SOI_REPORT_HEADER_INVESTMENT = "Investment";
    public static final String PARSE_SOI_REPORT_HEADER_TRANCHE = "Tranche";
    public static final String PARSE_SOI_REPORT_HEADER_TYPE = "Type: Fund, Secondary, Co-investment";
    public static final String PARSE_SOI_REPORT_HEADER_STRATEGY = "Strategy";
    public static final String PARSE_SOI_REPORT_HEADER_EXCHANGE_RATE = "Exchange rate \nin terms of USD";
    public static final String PARSE_SOI_REPORT_HEADER_CURRENCY = "Currency";
    public static final String PARSE_SOI_REPORT_HEADER_INVESTMENT_COMMITMENT = "Investment\nCommitment";
    public static final String PARSE_SOI_REPORT_HEADER_UNFUNDED_COMMITMENT = "Unfunded\nCommitment";
    public static final String PARSE_SOI_REPORT_HEADER_INVESTMENT_COMMITMENT_USD = "Investment\nCommitment\nUSD";
    public static final String PARSE_SOI_REPORT_HEADER_UNFUNDED_COMMITMENT_USD = "Unfunded\nCommitment\nUSD";
    public static final String PARSE_SOI_REPORT_HEADER_CONTRIBUTIONS_USD = "Contributions\nUSD";
    public static final String PARSE_SOI_REPORT_HEADER_RETURN_CAPITAL_DISTRIBUTIONS_USD = "Return of Capital\nDistributions\nUSD";
    public static final String PARSE_SOI_REPORT_HEADER_NET_COST_USD = "Net Cost\nUSD";
    public static final String PARSE_SOI_REPORT_HEADER_FAIR_VALUE_USD = "Fair Value\nUSD";
    public static final String PARSE_SOI_REPORT_HEADER_UNREALIZED_GAIN_LOSS_SINCE_INCEPTION_USD = "Unrealized\nGain (Loss)\nSince Inception\nUSD";
    public static final String PARSE_SOI_REPORT_HEADER_REALIZED_GAIN_LOSS_SINCE_INCEPTION_USD = "Realized\nGain (Loss)\nSince Inception\nUSD";
    public static final String PARSE_SOI_REPORT_HEADER_OPERATING_COMPANY = "Operating Company";
    public static final String PARSE_SOI_REPORT_HEADER_OWNERSHIP_DETAILS = "Ownership Details";

    /* STATEMENT OF ASSETS, LIABILITIES AND PARTNERS CAPITAL */
    public static final String STMT_ASST_LBLT_PC_TOTAL_RECORD_NAME_V1 = "Net increase (decrease) in partners' capital resulting from operations";
    public static final String STMT_ASST_LBLT_PC_TOTAL_RECORD_NAME_V2 = "Net increase (decrease) in partner's capital resulting from operations";
    public static final String STMT_ASST_LBLT_PC_TOTAL_RECORD_NAME_V3 = "Net increase (decrease) in partners capital resulting from operations";

    public static final String STMT_ASST_LBLT_PC_BALANCE_HEADER_V1 = "Consolidated Statement of Assets, Liabilities and Partners' Capital";
    public static final String STMT_ASST_LBLT_PC_BALANCE_HEADER_V2 = "Consolidated Statement of Assets, Liabilities and Partner's Capital";
    public static final String STMT_ASST_LBLT_PC_BALANCE_HEADER_V3 = "Consolidated Statement of Assets, Liabilities and Partners Capital";
    public static final String STMT_ASST_LBLT_PC_OPERATIONS_HEADER_V1 = "Consolidated Statement of Operations";

    public static final String STMT_ASST_LBLT_PC_ASSETS_CAPITAL_CASE = "ASSETS";
    public static final String STMT_ASST_LBLT_PC_LIABILITIES_CAPITAL_CASE = "LIABILITIES";
    public static final String STMT_ASST_LBLT_PC_PARTNERS_CAPITAL_CAPITAL_CASE_V1 = "PARTNERS' CAPITAL";
    public static final String STMT_ASST_LBLT_PC_PARTNERS_CAPITAL_CAPITAL_CASE_V2 = "PARTNER'S CAPITAL";
    public static final String STMT_ASST_LBLT_PC_PARTNERS_CAPITAL_CAPITAL_CASE_V3 = "PARTNERS CAPITAL";

    public static final String STMT_ASST_LBLT_PC_LIABILITIES_AND_PARTNERS_CAPITAL_CAPITAL_CASE_V1 = "LIABILITIES AND PARTNERS' CAPITAL";
    public static final String STMT_ASST_LBLT_PC_LIABILITIES_AND_PARTNERS_CAPITAL_CAPITAL_CASE_V2 = "LIABILITIES AND PARTNER'S CAPITAL";
    public static final String STMT_ASST_LBLT_PC_LIABILITIES_AND_PARTNERS_CAPITAL_CAPITAL_CASE_V3 = "LIABILITIES AND PARTNERS CAPITAL";

    public static final String TARR_OPERATIONS_TAX_EXPENSE_NAME = "FDAP tax expense";
    public static final String TARR_OPERATIONS_INCOME_TYPE_NET_REALIZED_GAIN_NAME = "Net realized gain on investments";
    public static final String TARR_OPERATIONS_INCOME_TYPE_NET_REALIZED_GAIN_LOSS_LONG_NAME = "Net realized gain (loss) on investments, including the effect of foreign currency translations";
    public static final String TARR_OPERATIONS_INCOME_TYPE_NET_CHANGE_UNREALIZED_APPR_DEPR_LONG_NAME = "Net change in unrealized appreciation / depreciation on investments, including the effect of foreign currency translations";
    public static final String TARR_OPERATIONS_INCOME_TYPE_DEFERRED_TAX_BENEFIT_NAME = "Deferred tax (expense) benefit";

    /* STATMENT OF CHANGES ********************************************************************************************/

    public static final String TARR_STATEMENT_CHANGES_CONTRIBUTIONS_RECEIVED = "Contributions received";
    public static final String TARR_STATEMENT_CHANGES_DISTRIBUTIONS_PAID = "Distributions paid";

    /* NOAL ***********************************************************************************************************/

    public static final String GROSVENOR_SUBSCRIPTION_ACCOUNT_NUMBER = "1500-XXXX-XXX-USD";
    public static final String GROSVENOR_REDEMPTION_ACCOUNT_NUMBER = "1550-XXXX-XXX-USD";

    public static final String BNY_MELLON_SUBSCRIPTION_ACCOUNT_NUMBER = null;
    public static final String BNY_MELLON_REDEMPTION_ACCOUNT_NUMBER = "1210-XXXX-XXX-USD";

    public static final String GCM_NOAL_TRANSACTION_ENDING_BALANCE_NAME_V1 = "Ending Balance";
    public static final String GCM_NOAL_TRANSACTION_ENDING_BALANCE_NAME_V2 = "Ending";

    public static final String BNY_MELON_NOAL_TOTAL_NAME = "Total";
    public static final String BNY_MELON_NOAL_REPORT_TOTAL_NAME = "Report Total";

    /* SINGULARITY ITD ******************************************************************************************************/
    public static final String PARSE_SINGULAR_ITD_HEADER_INVESTMENT_NAME = "Investment Name";
    public static final String PARSE_SINGULAR_ITD_HEADER_SUBSCRIPTIONS = "ITD Subs";
    public static final String PARSE_SINGULAR_ITD_HEADER_PROFIT_LOSS = "ITD P&L";
    public static final String PARSE_SINGULAR_ITD_HEADER_REDEMPTIONS = "ITD Reds";
    public static final String PARSE_SINGULAR_ITD_HEADER_CLOSING_BALANCE = "Closing Balance";

    public static final String PARSE_SINGULAR_ITD_HRS_HEADER_PERIOD = "Period";
    public static final String PARSE_SINGULAR_ITD_HRS_HEADER_MANAGER_FUND = "Manager Fund";
    public static final String PARSE_SINGULAR_ITD_HRS_HEADER_PORTFOLIO = "Portfolio";
    public static final String PARSE_SINGULAR_ITD_HRS_HEADER_NET_CONTRIBUTION = "Net Contribution";
    public static final String PARSE_SINGULAR_ITD_HRS_HEADER_ENDING_BALANCE = "Ending Balance";

    /* SINGULARITY GENERAL LEDGER V2 ******************************************************************************************************/
    public static final String PARSE_SINGULAR_GL_V2_HEADER_PORTFOLIO_ACRONYM = "Portfolio Acronym";
    public static final String PARSE_SINGULAR_GL_V2_HEADER_GROUP_CATEGORY = "GL FS Group Category";
    public static final String PARSE_SINGULAR_GL_V2_HEADER_GROUP_DESCRIPTION = "GL FS Group Description";
    public static final String PARSE_SINGULAR_GL_V2_HEADER_CATEGORY_NAME = "GL Category Name";
    public static final String PARSE_SINGULAR_GL_V2_HEADER_CATEGORY_LONG_NAME = "GL Category Long Name";
    public static final String PARSE_SINGULAR_GL_V2_HEADER_SHORT_NAME = "Short Name";
    public static final String PARSE_SINGULAR_GL_V2_HEADER_ENDING_BALANCE = "Ending Balance";
    public static final String PARSE_SINGULAR_GL_V2_HEADER_SEGVAL1= "Segval1";
    public static final String PARSE_SINGULAR_GL_V2_HEADER_SEGVAL_CCY = "Seg Val CCY";



    /* SPV NAMES *********************************************************************************************/
    public static final String SINGULAR_CAPITAL_CASE = "SINGULAR";
    public static final String SINGULAR_B_CAPITAL_CASE = "SINGULAR B";

    public static final String SINGULAR_LOWER_CASE = "Singular";

    public static final String SINGULARITY_LOWER_CASE = "Singularity";
    public static final String SINGULARITY_A_LOWER_CASE = "Singularity A";
    public static final String SINGULARITY_B_LOWER_CASE = "Singularity B";

    public static final String TARRAGON_CAPITAL_CASE = "TARRAGON";
    public static final String TARRAGON_B_CAPITAL_CASE = "TARRAGON B";
    public static final String TARRAGON_LOWER_CASE = "Tarragon";

    public static final String TARRAGON_A_LOWER_CASE ="Tarragon A";

    public static final String TERRA_CAPITAL_CASE = "TERRA";
    public static final String TERRA_LOWER_CASE = "Terra";

    public static final String TRANCHE_LOWER_CASE = "Tranche";
    public static final String TRANCHE_A_LOWER_CASE = "Tranche A";

    /* REPORT TYPE CODE ***********************************************************************************************/
    public static final String USD_FORM_1 = "CONS_BALANCE_USD";
    public static final String USD_FORM_2 = "INCOME_EXP_USD";
    public static final String USD_FORM_3 = "TOTAL_INCOME_USD";
    public static final String KZT_FORM_1 = "KZT_FORM_1";
    public static final String KZT_FORM_2 = "KZT_FORM_2";
    public static final String KZT_FORM_3 = "KZT_FORM_3";
    public static final String KZT_FORM_6 = "KZT_FORM_6";
    public static final String KZT_FORM_7 = "KZT_FORM_7";
    public static final String KZT_FORM_8 = "KZT_FORM_8";
    public static final String KZT_FORM_10 = "KZT_FORM_10";
    public static final String KZT_FORM_13 = "KZT_FORM_13";
    public static final String KZT_FORM_14 = "KZT_FORM_14";
    public static final String KZT_FORM_19 = "KZT_FORM_19";
    public static final String KZT_FORM_22 = "KZT_FORM_22";
    /* ****************************************************************************************************************/

    /* GENERATED REPORT KZT LITERALS **********************************************************************************/
    public static final String SUBACCOUNT_GROUP_NUMBER = "Номер группы субсчетов";
    public static final String SUBACCOUNT_GROUP_NAME = "Наименование группы субсчетов";
    public static final String LINE_CODE = "Код строки";
    public static final String INDICATORS_NAME = "Наименование показателей";
    public static final String FIN_INVESTMENT_TYPE = "Вид финансовых инвестиций";
    public static final String ON_CURRENT_PERIOD_DATE = "На отчетную дату текущего периода";
    public static final String DEBTOR_NAME = "Наименование дебитора";
    public static final String ASSETS_DEBTOR_NAME = "Наименование актива/дебитора";
    public static final String CREDITOR_NAME = "Наименование кредитора";
    public static final String FIN_LIABILITIES_TYPE = "Вид финансовых обязательств";
    public static final String INCOME_EXPENSE_TYPE = "Вид дохода/расхода";

    public static final String KZT_REPORT_HEADER_DATE_PLACEHOLDER = "по состоянию на дату";
    public static final String KZT_REPORT_HEADER_DATE_PLACEHOLDER_DATE_ONLY = "<dd.MM.yyyy>";
    public static final String KZT_REPORT_HEADER_DATE_TEXT = "по состоянию на ";

    public static final String RU_INVESTMENTS_TO_RETURN = "Инвестиции к возврату";
    public static final String RU_PRE_SUBSCRIPTION = "Предварительная подписка";
    public static final String INCOME_FAIR_VALUE_CHANGES = "Доходы от изменения справедливой стоимости долгосрочных финансовых инвестиций, имеющихся в наличии для продажи";
    public static final String EXPENSE_FAIR_VALUE_CHANGES = "Расходы от изменения справедливой стоимости долгосрочных финансовых инвестиций, имеющихся в наличии для продажи";
    public static final String RU_EXPENSES_FUTURE_PERIOD = "Расходы будущих периодов";
    public static final String RU_PE_FUND_INVESTMENT = "Инвестиции в фонд частного капитала";
    public static final String RU_HEDGE_FUND_INVESTMENT = "Инвестиции в хедж-фонд";
    public static final String RU_REAL_ESTATE_FUND_INVESTMENT = "Инвестиции в фонд недвижимости";

    public static final String EN_NOAL_PORTFOLIO_REDEMPTION = "Portfolio Fund Redemption/Withdrawal - Corp";
    public static final String EN_NET_REALIZED_GAIN_LOSS = "Net Realized Gains/Losses";
    public static final String SINGULARITY_AGREEMENT_DESC = "Investment Management Agreement of Singularity Ltd. from 14.07.2015";
    public static final String TARRAGON_AGREEMENT_DESC = "Limited Partnership Agreement Tarragon LP from 18.12.2014";
    public static final String NICK_MF_AGREEMENT_DESC = "Administrative services agreement of NICK Master Fund from 24.07.2015";
    public static final String TERRA_AGREEMENT_DESC = "Limited Partnership Agreement Terra LP from 06.04.2018";

    public static final String INVESTMENT_IN_PORTFOLIO_FUNDS = "Investments in Portfolio Funds";
    public static final String INVESTOR_SUBSCR_RECEIVED_IN_ADVANCE = "Investor Subscription Received in Advance";
    public static final String NET_CHANGE_UNREALIZED_GAINS_LOSSES = "Net Change in Unrealized Gains/Losses";


    public static final String BANK_LOANS_RECEIVED = "Банковские займы полученные";
    public static final String BANK_OF_MONREAL = "Bank of Monreal";
    public static final String DEFAULT_KZT_13_INTEREST_RATE = "4%";
    public static final String RU_KZT_6_REPORT_LINE_NUMBER_2_NAME = "Изменения в учетной политике и ошибки";
    public static final String RU_KZT_6_REPORT_LINE_NUMBER_2_7_CODE = "KZT6_2_7";
    public static final String RU_KZT_6_REPORT_LINE_NUMBER_2_8_CODE = "KZT6_2_8";
    public static final String RU_KZT_3_REPORT_LINE_NUMBER_5_1_CODE = "KZT3_5_1";
    /* ****************************************************************************************************************/



    /* ACCOUNT NUMBERS AND NAMES **************************************************************************************/
    public static final String GROSVENOR_ACCOUNT_NUMBER_1500 = "1500"; // Subscriptions
    public static final String GROSVENOR_ACCOUNT_NUMBER_1550 = "1550"; // Redemptions
    // TODO: ??? for NOAL
    //public static final String GROSVENOR_ACCOUNT_NUMBER_1500 = "1500"; // Subscriptions
    public static final String GROSVENOR_ACCOUNT_NUMBER_1210 = "1210"; // Redemptions


    public static final String ACC_NUM_5440_010 = "5440.010";
    // TODO: when a when b?
    public static final String RU_5440_010_a = "Резерв по переоценке финансовых инвестиций, имеющихся в наличии для продажи";

    //public static final String RU_5440_010_b = "Резерв на переоценку  финансовых инвестиций, имеющихся в наличии для продажи";
    public static final String RU_5440_010_b = "Резерв на переоценку финансовых активов, оцениваемых по справедливой стоимости через прочий совокупный доход";
    public static final String RU_5440_010__LAST_YEAR = "Резерв по переоценке финансовых инвестиций, имеющихся в наличии для продажи (прошлый год)";


    public static final String ACC_NUM_5520_010 = "5520.010";
    public static final String RU_5520_010 = "Нераспределенная прибыль (непокрытый убыток) прошлых лет";

    public static final String ACC_NUM_5510_010 = "5510.010";
    public static final String RU_5510_010 = "Нераспределенная прибыль (непокрытый убыток) отчетного года";

    public static final String ACC_NUM_6150_030 = "6150.030";
    public static final String ACC_NUM_7330_030 = "7330.030";

    public static final String ACC_NUM_7470_010 = "7470.010";

    public static final String ACC_NUM_1033_010 = "1033.010";
    public static final String RU_1033_010 = "Деньги на текущих счетах";

    public static final String ACC_NUM_1123_010 = "1123.010";
    public static final String RU_1123_010 = "Краткосрочные ценные бумаги, оцениваемые по справедливой стоимости через прибыль или убыток";

    public static final String ACC_NUM_1123_020 = "1123.020";
    public static final String RU_1123_020 = "Положительная корректировка справедливой стоимости краткосрочных ценных бумаг, оцениваемых по справедливой стоимости через прибыль или убыток";

    public static final String ACC_NUM_1123_030 = "1123.030";
    public static final String RU_1123_030 = "Отрицательная корректировка справедливой стоимости краткосрочных ценных бумаг, оцениваемых по справедливой стоимости через прибыль или убыток";


    public static final String ACC_NUM_1283_020 = "1283.020";
    public static final String RU_1283_020 = "Прочая краткосрочная дебиторская задолженность";

    public static final String ACC_NUM_1183_040 = "1183.040";
    public static final String RU_1183_040 = "Начисленные доходы в виде вознаграждения по краткосрочным финансовым активам, оцениваемым по справедливой стоимости через прибыль или убыток";

    public static final String ACC_NUM_1623_010 = "1623.010";
    public static final String ACC_NUM_2923_010 = "2923.010";

    public static final String ACC_NUM_2033_010 = "2033.010";
    public static final String RU_2033_010_old = "Долгосрочные ценные бумаги, имеющиеся в наличии для продажи";
    public static final String RU_2033_010 = "Долгосрочные ценные бумаги, оцениваемые по справедливой стоимости через прочий совокупный доход";

    public static final String ACC_NUM_2033_040 = "2033.040";
    public static final String RU_2033_040_old = "Положительная корректировка справедливой стоимости долгосрочных ценных бумаг, имеющихся в наличии для продажи";
    public static final String RU_2033_040 = "Положительная корректировка справедливой стоимости долгосрочных ценных бумаг, оцениваемых по справедливой стоимости через прочий совокупный доход";

    public static final String ACC_NUM_2033_050 = "2033.050";
    public static final String RU_2033_050_old = "Отрицательная корректировка справедливой стоимости долгосрочных ценных бумаг, имеющихся в наличии для продажи";
    public static final String RU_2033_050 = "Отрицательная корректировка справедливой стоимости долгосрочных ценных бумаг, оцениваемых по справедливой стоимости через прочий совокупный доход";

    public static final String ACC_NUM_3013_010 = "3013.010";
    public static final String RU_3013_010 = "Краткосрочные банковские займы полученные";

    public static final String ACC_NUM_3383_010 = "3383.010";
    public static final String RU_3383_010 = "Начисленные расходы в виде вознаграждения по краткосрочным банковским займам полученным";

    public static final String ACC_NUM_3063_010 = "3063.010";
    public static final String RU_3063_010 = "Начисленные расходы в виде вознаграждения по краткосрочным банковским займам полученным";

    public static final String ACC_NUM_3393_020 = "3393.020";
    public static final String RU_3393_020 = "Прочая краткосрочная кредиторская задолженность";

    public static final String ACC_NUM_4173_010 = "4173.010";
    public static final String RU_4173_010 = "Прочая долгосрочная кредиторская задолженность";

    public static final String ACC_NUM_5021_010 = "5021.010";
    public static final String ACC_NUM_5022_010 = "5022.010";
    public static final String COMMON_SHARES = "Простые акции";

    public static final String ACC_NUM_5450_010 = "5450.010";
    public static final String RU_5450_010 = "Резерв на пересчет иностранной валюты по зарубежной деятельности";
    public static final String ACC_NUM_5450_010_CODE_ADJUSTMENT = "KZT1_51_adj";
    public static final String ACC_NUM_5450_010_CODE_ADJUSTMENT_RU = "Резерв на пересчет иностранной валюты по зарубежной деятельности (Корректировка ОФП-1)";

    public static final String ACC_NUM_6150_020 = "6150.020";
    public static final String ACC_NUM_6150_010 = "6150.010";
    public static final String ACC_NUM_6113_030 = "6113.030";
    public static final String ACC_NUM_6280_010 = "6280.010";
    public static final String ACC_NUM_6283_080 = "6283.080";

    public static final String ACC_NUM_7313_010 = "7313.010";
    public static final String RU_7313_010 = "Расходы по вознаграждению по краткосрочным банковским займам";

    public static final String ACC_NUM_7330_010 = "7330.010";

    public static final String ACC_NUM_7330_020 = "7330.020";

    public static final String ACC_NUM_7473_080 = "7473.080";

    public static final String ACC_NUM_3053_060 = "3053.060";
    public static final String RU_3053_060 = "Прочие краткосрочные финансовые обязательства";
    public static final String RU_3053_060_FORM_13 = "Прочие финансовые обязательства";
    /* ****************************************************************************************************************/



    /* GENERATED REPORT KZT RECORD NAMES ********************************************************************************************/
    public static final String USD_FORM_1_LAST_RECORD = "Всего обязательства и капитал (сумма строк 37, 45, 53)";
    public static final String USD_FORM_2_LAST_RECORD = "Чистая прибыль (убыток) (сумма строк 19, 20)";
    public static final String USD_FORM_3_LAST_RECORD = "Всего совокупного дохода (сумма строк 1, 12)";
//    public static final String KZT_FORM_3_LAST_RECORD = "Итого совокупного дохода (сумма строк 1, 6)";
    public static final String KZT_FORM_3_LAST_RECORD = "Всего совокупного дохода (сумма строк 1, 12)";
    public static final String KZT_FORM_6_LAST_RECORD = "Остаток на конец текущего отчетного периода (сумма строк 3, 6, 14)";
    public static final String KZT_FORM_7_LAST_RECORD = "ВСЕГО (сумма строк 1, 8)";
    public static final String KZT_FORM_8_LAST_RECORD = "Всего (сумма строк 1, 7)";
    public static final String KZT_FORM_10_LAST_RECORD = "Всего  (сумма строк 1, 5)";
    public static final String KZT_FORM_13_LAST_RECORD = "Всего (сумма строк 1, 4)";
    public static final String KZT_FORM_14_LAST_RECORD = "Всего (сумма строк 1, 6)";
    public static final String KZT_FORM_19_LAST_RECORD = "Всего (сумма строк 1, 8, 18, 23, 30, 33, 38 и 43)";
    public static final String KZT_FORM_22_LAST_RECORD = "Всего (сумма строк 1, 2)";
    /* ****************************************************************************************************************/
}
