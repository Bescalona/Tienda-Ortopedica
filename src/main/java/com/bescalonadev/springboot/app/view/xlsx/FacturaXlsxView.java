package com.bescalonadev.springboot.app.view.xlsx;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

import com.bescalonadev.springboot.app.models.entity.Factura;
import com.bescalonadev.springboot.app.models.entity.ItemFactura;

@Component("factura/ver.xlsx")
public class FacturaXlsxView extends AbstractXlsxView{

	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		//Cambiar el nombre del archivo
		response.setHeader("Content-Disposition", "attachment; filename=\"factura_view.xlsx\"");
		
		//"factura" debe llamarse igual que en el controlador FacturaController-ver
		Factura factura = (Factura) model.get("factura");
		Sheet sheet = workbook.createSheet("Factura Spring");
		
		//Para darle estilo a las tablas
		CellStyle theaderStyle = workbook.createCellStyle();
		theaderStyle.setBorderBottom(BorderStyle.MEDIUM);
		theaderStyle.setBorderTop(BorderStyle.MEDIUM);
		theaderStyle.setBorderRight(BorderStyle.MEDIUM);
		theaderStyle.setBorderLeft(BorderStyle.MEDIUM);
		theaderStyle.setFillForegroundColor(IndexedColors.GOLD.index);
		theaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		CellStyle tbodyStyle = workbook.createCellStyle();
		tbodyStyle.setBorderBottom(BorderStyle.THIN);
		tbodyStyle.setBorderTop(BorderStyle.THIN);
		tbodyStyle.setBorderRight(BorderStyle.THIN);
		tbodyStyle.setBorderLeft(BorderStyle.THIN);
		
		Row row = sheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue("Datos de la factura");
		
		sheet.createRow(1).createCell(0).setCellValue("Folio: " + factura.getId());
		sheet.createRow(2).createCell(0).setCellValue("Nombre: "+ factura.getNombre());
		sheet.createRow(3).createCell(0).setCellValue("Fecha: "+ factura.getCreateAt());
		
		Row header = sheet.createRow(5);
		header.createCell(0).setCellValue("Producto");
		header.createCell(1).setCellValue("Categoria");
		header.createCell(2).setCellValue("Precio");
		header.createCell(3).setCellValue("Cantidad");
		header.createCell(4).setCellValue("Total");
		
		header.getCell(0).setCellStyle(theaderStyle);
		header.getCell(1).setCellStyle(theaderStyle);
		header.getCell(2).setCellStyle(theaderStyle);
		header.getCell(3).setCellStyle(theaderStyle);
		header.getCell(4).setCellStyle(theaderStyle);
		
		int rownum = 6;
		
		for(ItemFactura item: factura.getItems()) {
			Row fila = sheet.createRow(rownum++);
			cell = fila.createCell(0);
			cell.setCellValue(item.getProducto().getNombre());
			cell.setCellStyle(tbodyStyle);
			
			cell =  fila.createCell(1);
			cell.setCellValue(item.getProducto().getCategoria().getNombre());
			cell.setCellStyle(tbodyStyle);
			
			cell =  fila.createCell(2);
			cell.setCellValue(item.precioFormateado());
			cell.setCellStyle(tbodyStyle);
			
			cell = fila.createCell(3);
			cell.setCellValue(item.getCantidad());
			cell.setCellStyle(tbodyStyle);
			
			cell = fila.createCell(4);
			cell.setCellValue(item.totalFormateado());
			cell.setCellStyle(tbodyStyle);
		}
		
		Row filatotal = sheet.createRow(rownum);
		
		cell = filatotal.createCell(3);
		cell.setCellValue("Gran Total: ");
		cell.setCellStyle(tbodyStyle);
		
		cell = filatotal.createCell(4);
		cell.setCellValue(factura.getTotal());
		cell.setCellStyle(tbodyStyle);
	}

}
