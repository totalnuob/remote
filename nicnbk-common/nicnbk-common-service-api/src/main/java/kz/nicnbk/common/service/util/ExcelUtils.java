package kz.nicnbk.common.service.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 * Created by magzumov on 15.05.2017.
 */
public class ExcelUtils {

    public static boolean isEmptyCell(Cell cell){
        if(cell == null){
            return true;
        }else if(cell.getCellType() == Cell.CELL_TYPE_BLANK ||
                (cell.getCellType() == Cell.CELL_TYPE_STRING && StringUtils.isEmpty(cell.getStringCellValue()))){
            return true;
        }
        return false;
    }

    public static boolean isNotEmptyCell(Cell cell){
        return !isEmptyCell(cell);
    }

    public static boolean isEmptyCellRange(Row row, int from, int to){
        for(int i = from; i <= to; i++){
            Cell cell = row.getCell(i);
            if(isNotEmptyCell(cell)){
                return false;
            }
        }
        return true;
    }

    public static boolean isNotEmptyCellRange(Row row, int from, int to){
        return !isEmptyCellRange(row, from, to);
    }

    public static Double getDoubleValueFromCell(Cell cell){
        if(cell == null){
            return null;
        }else if(cell != null && cell.getCellType() == Cell.CELL_TYPE_NUMERIC || cell.getCellType() == Cell.CELL_TYPE_FORMULA){
            return cell.getNumericCellValue();
        }else if(cell.getCellType() == Cell.CELL_TYPE_STRING){
            try{
                if(cell.getStringCellValue().startsWith("(") && cell.getStringCellValue().endsWith(")")){
                    int length = cell.getStringCellValue().length();
                    Double value = Double.parseDouble(cell.getStringCellValue().substring(1, length - 1));
                    return (0 - value) ;
                }
                Double value = Double.parseDouble(cell.getStringCellValue());
                return value;
            }catch (Exception ex){

                // TODO: log error
                //System.out.println("Double parse failed: " + cell.getStringCellValue());

            }
        }
        return null;
    }

    public static String getStringValueFromCell(Cell cell){
        if(cell == null || cell.getCellType() != Cell.CELL_TYPE_STRING){
            return null;
        }else{
            return cell.getStringCellValue();
        }
    }

    public static String getTextValueFromAnyCell(Cell cell){
        if(cell == null){
            return null;
        }else if(cell.getCellType() == Cell.CELL_TYPE_STRING){
            return cell.getStringCellValue();

        }else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
            return cell.getNumericCellValue() + "";
        }else if(cell.getCellType() == Cell.CELL_TYPE_BLANK){
            return "";
        }else if(cell.getCellType() == Cell.CELL_TYPE_BOOLEAN){
            return cell.getBooleanCellValue() + "";
        }else if(cell.getCellType() == Cell.CELL_TYPE_ERROR){
            return null;
        }else if(cell.getCellType() == Cell.CELL_TYPE_FORMULA){
            return cell.getCellFormula();
        }else {
            return null;
        }
    }

    public static boolean isCellStringValueEqual(Cell cell, String value){
        if(cell == null || cell.getCellType() != Cell.CELL_TYPE_STRING || StringUtils.isEmpty(cell.getStringCellValue())){
            return false;
        }
        return cell.getStringCellValue().trim().equals(value);
    }

    public static boolean isCellStringValueEqualIgnoreCase(Cell cell, String value){
        if(cell == null || cell.getCellType() != Cell.CELL_TYPE_STRING || StringUtils.isEmpty(cell.getStringCellValue())){
            return false;
        }
//        for(int i = 0; i < value.length(); i++){
//
//            if(value.charAt(i) != cell.getStringCellValue().trim().charAt(i)){
//                System.out.println("MISMATCH");
//                System.out.println(value.charAt(i));
//                System.out.println(cell.getStringCellValue().trim().charAt(i));
//            }
//        }
        return cell.getStringCellValue().trim().equalsIgnoreCase(value);
    }

    public static String getCellCurrency(Cell cell){
        if(cell.getCellStyle().getDataFormatString().contains("€")){
            return "EUR";
        }else if(cell.getCellStyle().getDataFormatString().contains("£")){
            return "GBP";
        }else if(cell.getCellStyle().getDataFormatString().contains("$")){
            return "USD";
        }else{
            return null;
        }
    }

}
