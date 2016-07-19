package com.tsystems.javaschool.uberbahnreports;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.ejb.Stateless;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.LocalDateTime;
import java.util.Collection;

@Stateless
public class PDF {

    private static Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
            Font.BOLD, new BaseColor(70, 92, 113));
    private static Font tableHeadingFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.BOLD, new BaseColor(255, 255, 255));
    private static BaseColor tableHeadingBackground = new BaseColor(70, 92, 113);
    private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.


    public void uploadPDFToClient(File pdfFile) throws IOException {
        // Prepare.
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
        System.out.println(pdfFile.getAbsolutePath() + " " + pdfFile.getName());
        File file = new File(pdfFile.getName());
//		File file = new File(getFileName());
        BufferedInputStream input = null;
        BufferedOutputStream output = null;

        try {
            // Open file.
            input = new BufferedInputStream(new FileInputStream(file), DEFAULT_BUFFER_SIZE);

            // Init servlet response.
            response.reset();
            response.setHeader("Content-Type", "application/pdf");
            response.setHeader("Content-Length", String.valueOf(file.length()));
            response.setHeader("Content-Disposition", "inline; filename=\"" + pdfFile.getName() + "\"");
            output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);

            // Write file contents to response.
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            // Finalize task.
            output.flush();
        } finally {
            // Gently close streams.
            close(output);
            close(input);
        }

        // Inform JSF that it doesn't need to handle response.
        // This is very important, otherwise you will get the following exception in the logs:
        // java.lang.IllegalStateException: Cannot forward after response has been committed.
        facesContext.responseComplete();
    }

    private void close(Closeable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public File createPdf(Collection<TicketInfo> ticketInfos, LocalDateTime datetimeSince, LocalDateTime datetimeUntil) {

        String pdfFileName = "table"+(int)(1000000*Math.random()) + ".pdf";
        File pdfFile = new File(pdfFileName);

        try {
            Document document = new Document(PageSize.A4.rotate(), 0, 0, 15f, 0);

            PdfWriter.getInstance(document,
                    new FileOutputStream(pdfFileName));
            document.open();
            addTitle(document, datetimeSince, datetimeUntil);
            if (ticketInfos.size() == 0) {
                Paragraph paragraph = new Paragraph();
                paragraph.add(new Paragraph("No tickets purchased", titleFont));
                paragraph.setSpacingBefore(25);
                paragraph.setIndentationLeft(50);
                addEmptyLine(paragraph, 1);
                document.add(paragraph);
            } else {
                document.add(createTable(ticketInfos));
            }
            document.close();

        } catch(Exception e){
            e.printStackTrace();
        }
        return pdfFile;
    }

    private static PdfPTable createTable(Collection<TicketInfo> ticketInfos)
            throws BadElementException {
        PdfPTable table = new PdfPTable(9);

        PdfPCell c1 = new PdfPCell(new Phrase("Number", tableHeadingFont));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setVerticalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(tableHeadingBackground);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Train", tableHeadingFont));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setVerticalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(tableHeadingBackground);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Route Title", tableHeadingFont));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setVerticalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(tableHeadingBackground);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Departure", tableHeadingFont));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setVerticalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(tableHeadingBackground);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Arrival", tableHeadingFont));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setVerticalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(tableHeadingBackground);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Passenger", tableHeadingFont));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setVerticalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(tableHeadingBackground);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Date and Time of Departure", tableHeadingFont));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setVerticalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(tableHeadingBackground);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Price", tableHeadingFont));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setVerticalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(tableHeadingBackground);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Login", tableHeadingFont));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setVerticalAlignment(Element.ALIGN_CENTER);
        c1.setBackgroundColor(tableHeadingBackground);
        table.addCell(c1);

        table.setHeaderRows(1);

        ticketInfos.forEach(ticketInfo -> {
            table.addCell(String.valueOf(ticketInfo.getId()));
            table.addCell(String.valueOf(ticketInfo.getTrainId()));
            table.addCell(ticketInfo.getRouteTitle());
            table.addCell(ticketInfo.getStationOfDeparture() + " " + ticketInfo.getDateOfDeparture() + " " + ticketInfo.getTimeOfDeparture());
            table.addCell(ticketInfo.getStationOfArrival() + " " + ticketInfo.getDateOfArrival() + " " + ticketInfo.getTimeOfArrival());
            table.addCell(ticketInfo.getFirstName() + " " + ticketInfo.getLastName() + " " + ticketInfo.getDateOfBirth());
            table.addCell(ticketInfo.getDateOfPurchase() + " " + ticketInfo.getTimeOfPurchase());
            table.addCell(String.valueOf(ticketInfo.getPrice()));
            table.addCell(ticketInfo.getLogin());
        });

        return table;

    }


    private static void addTitle(Document document, LocalDateTime since, LocalDateTime until)
            throws DocumentException {
        Paragraph titleParagraph = new Paragraph();
        titleParagraph.add(new Paragraph(("Report " + since.toLocalDate() + " - " + until.toLocalDate()), titleFont));
        titleParagraph.setSpacingBefore(25);
        titleParagraph.setIndentationLeft(50);
        titleParagraph.setAlignment(Element.ALIGN_CENTER);
        addEmptyLine(titleParagraph, 1);
        document.add(titleParagraph);
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }



}
