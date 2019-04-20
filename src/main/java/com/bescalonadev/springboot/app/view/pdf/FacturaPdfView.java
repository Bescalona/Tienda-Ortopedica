package com.bescalonadev.springboot.app.view.pdf;

import java.awt.Color;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import com.bescalonadev.springboot.app.models.entity.Factura;
import com.bescalonadev.springboot.app.models.entity.ItemFactura;
import com.lowagie.text.Document;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

//Misma ruta ("factura/ver") que devuelve el ver del FacturaController
@Component("factura/ver")
public class FacturaPdfView extends AbstractPdfView{

	@Override
	protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		Factura factura = (Factura) model.get("factura");
		
		//Creamos el objeto celda para customizar su apariencia 
		PdfPCell cell = null;
		
		PdfPTable tabla2 = new PdfPTable(1);
		tabla2.setSpacingAfter(20);
		cell = new PdfPCell(new Phrase("Datos de la Factura"));
		
		//En vez de añadir celda ahora creamos la celda para poder modificarla
		cell.setBackgroundColor(new Color(81,180,255));
		cell.setPadding(8f);
		tabla2.addCell(cell);
		//tabla2.addCell("Datos de la Factura");
		
		tabla2.addCell("Folio: "+factura.getId());
		tabla2.addCell("Nombre: "+factura.getNombre());
		tabla2.addCell("Fecha: "+factura.getCreateAt());

		document.add(tabla2);
		
		PdfPTable tabla3 = new PdfPTable(5);
		//Cambiamos el ancho de las columnas
		tabla3.setWidths(new float[] {3.5f,1.4f,1.3f,1.2f,1.4f});
		
		cell = new PdfPCell(new Phrase("Producto"));
		cell.setBackgroundColor(new Color(81,180,255));
		tabla3.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Categoria"));
		cell.setBackgroundColor(new Color(81,180,255));
		tabla3.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Precio"));
		cell.setBackgroundColor(new Color(81,180,255));
		tabla3.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Cantidad"));
		cell.setBackgroundColor(new Color(81,180,255));
		tabla3.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Total"));
		cell.setBackgroundColor(new Color(81,180,255));
		tabla3.addCell(cell);
		
		for(ItemFactura item: factura.getItems()) {
			tabla3.addCell(item.getProducto().getNombre());
			tabla3.addCell(item.getProducto().getCategoria().getNombre());
			tabla3.addCell(item.getProducto().precioFormateado());
			//Creamos el objeto celda para alinar la cantidad
			
			cell = new PdfPCell(new Phrase(item.getCantidad().toString()));
			cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			tabla3.addCell(cell);
			tabla3.addCell(item.totalFormateado());
		}
		
		cell = new PdfPCell(new Phrase("Gran Total: "));
		cell.setColspan(4);
		cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
		tabla3.addCell(cell);
		tabla3.addCell(factura.getTotal().toString());
		
		document.add(tabla3);
	}
	
}
