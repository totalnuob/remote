package kz.nicnbk.common.service.util;

import org.apache.poi.ss.usermodel.Cell;

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

    public static Double getDoubleValueFromCell(Cell cell){
        if(cell == null){
            return null;
        }else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC || cell.getCellType() == Cell.CELL_TYPE_FORMULA){
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
                System.out.println("Double parse failed: " + cell.getStringCellValue());

            }
        }
        return null;
    }

    public static boolean isCellStringValueEqual(Cell cell, String value){
        if(cell == null || cell.getCellType() != Cell.CELL_TYPE_STRING || StringUtils.isEmpty(cell.getStringCellValue())){
         return false;
        }
        return cell.getStringCellValue().equals(value);
    }

}
