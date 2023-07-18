package com.framework.factory;

import com.framework.DynamicTest;
import com.framework.baseFunctions.BaseController;
import com.framework.services.MyConfig;
import com.framework.utilities.FilesUtil;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.poi.ss.usermodel.Sheet;

import java.awt.*;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PDFReportsFactory {
    protected ThreadLocal<String> trdStrPath = new ThreadLocal<>();
    protected ThreadLocal<String> trdStrTitle = new ThreadLocal<>();
    protected ThreadLocal<String> trdStrWPFileName = new ThreadLocal<>();
    protected ThreadLocal<String> trdStrExcelWPFile = new ThreadLocal<>();
    private static volatile PDFReportsFactory instance = null;
    private PDDocument doc = null;
    private PDFont font = null;

    /**
     * constructor
     */
    private PDFReportsFactory() {
        trdStrTitle.set(FilesUtil.init().getDatatableFileName());
        trdStrPath.set(MyConfig.strPathReport + trdStrTitle.get() + "\\Report\\");
        if (MyConfig.boolCreateWPByData) {
            trdStrWPFileName.set("WP-" + DynamicTest.trdIntNoData.get() + "_" + FilesUtil.init().getDatatableFileName() + ".pdf");
            trdStrExcelWPFile.set(trdStrPath.get() + "WP-" + DynamicTest.trdIntNoData.get() + "_" + FilesUtil.init().getDatatableFile());
        } else {
            trdStrWPFileName.set("WP-" + FilesUtil.init().getDatatableFileName() + ".pdf");
            trdStrExcelWPFile.set(trdStrPath.get() + "WP-" + FilesUtil.init().getDatatableFile());
        }
    }

    public static PDFReportsFactory init() {
        if (instance == null) {
            synchronized (ExtentReportsFactory.class) {
                if (instance == null)
                    instance = new PDFReportsFactory();
            }
        }
        return instance;
    }

    /**
     * create new PDF File
     */
    public void createPdfFile() {
        File fileWPPDF = new File(trdStrPath.get() + trdStrWPFileName.get());

        if (fileWPPDF.exists()) {
            fileWPPDF.delete();
        }

        doc = new PDDocument();
        
        try {
            font = PDType0Font.load(doc, new File("/Windows/Fonts" +"/calibri.ttf"));
        } catch (IOException e) {
            font = PDType1Font.HELVETICA;
            e.printStackTrace();
        }
        save();
    }

    /**
     * creating PDF report
     */
    public void createPDFReport() {
        String strImagePath = MyConfig.strPathReport + trdStrTitle.get() + "\\Screenshot\\";
        File ImagePath = new File(strImagePath);

        try {
            File[] matchingFiles = ImagePath.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith("png");
                }
            });

            List<List<String>> listImageFile = new ArrayList<List<String>>();
            List<List<String>> arrStrWPName = new ArrayList<List<String>>();

            int intCounterImageFileName = 0;

            if (matchingFiles != null && matchingFiles.length != 0) {
                Arrays.sort(matchingFiles);

                listImageFile.add(new ArrayList<String>());
                arrStrWPName.add(new ArrayList<String>());

                for (int i = 0; i < matchingFiles.length; i++) {
                    if (i == 0) {
                        listImageFile.get(intCounterImageFileName).add(matchingFiles[i].getName());
                        arrStrWPName.get(intCounterImageFileName).add(matchingFiles[i].getName().split("_")[0]);
                    } else if (matchingFiles[i].getName().split("_")[0].equalsIgnoreCase(matchingFiles[i - 1].getName().split("_")[0])) {
                        listImageFile.get(intCounterImageFileName).add(matchingFiles[i].getName());
                    } else {
                        intCounterImageFileName++;
                        listImageFile.add(new ArrayList<>());
                        arrStrWPName.add(new ArrayList<>());
                        arrStrWPName.get(intCounterImageFileName).add(matchingFiles[i].getName().split("_")[0]);
                        listImageFile.get(intCounterImageFileName).add(matchingFiles[i].getName());
                    }
                }
            }

            Sheet sheetDatatable = ExcelFactory.init().readSpecificSheet(trdStrExcelWPFile.get(), MyConfig.strMainSheet);
            int intColumnScenario = ExcelFactory.init().getColumnByName(sheetDatatable, "SCENARIO");

            if (ExcelFactory.init().sheetIsExist(trdStrExcelWPFile.get(), "Report API")) {
                Sheet sheetReport = ExcelFactory.init().readSpecificSheet(trdStrExcelWPFile.get(), "Report API");

                int intRowCount = sheetReport.getLastRowNum() - sheetReport.getFirstRowNum();
                int intColumnStatusData = ExcelFactory.init().getColumnByName(sheetReport, "STATUS");

                String strHeading = "";
                StringBuffer StrBuffPageText = new StringBuffer();

                if (!MyConfig.boolCreateWPByData) {
                    createSummaryTable(sheetReport);
                }

                for (int j = 1; j <= intRowCount; j++) {
                    try {
                        strHeading = "Report Data ke - " + j;

                        StrBuffPageText.append(System.getProperty("line.separator"));
                        StrBuffPageText.append("Status -> ;" + sheetReport.getRow(j).getCell(intColumnStatusData).toString());
                        for (int k = 1; k <= intColumnStatusData; k++) {
                            if (k != intColumnStatusData) {
                                StrBuffPageText.append(System.getProperty("line.separator"));
                                StrBuffPageText.append(sheetReport.getRow(0).getCell(k).toString().replace("\n", "").replace("\r", "") + " -> " + sheetReport.getRow(j).getCell(k).toString().replace("\n", "").replace("\r", ""));
                            }
                        }
                        StrBuffPageText.append(System.getProperty("line.separator"));

                        if (listImageFile.size() != 0) {
                            Boolean boolNoMatch = true;

                            for (int i = 0; i <= intCounterImageFileName; i++) {
                                if (Integer.parseInt(arrStrWPName.get(i).get(0).split("-")[0]) == j) {
                                    addPage(strHeading, StrBuffPageText, strImagePath, listImageFile.get(i), PDType1Font.COURIER, 12);
                                    boolNoMatch = false;
                                }
                            }
                            if (boolNoMatch) {
                                addPage(strHeading, StrBuffPageText, strImagePath, null, PDType1Font.COURIER, 12);
                            }
                        } else {
                            addPage(strHeading, StrBuffPageText, strImagePath, null, PDType1Font.COURIER, 12);
                        }
                        StrBuffPageText.delete(0, StrBuffPageText.length());
                    } catch (NullPointerException e) {
                        StrBuffPageText.delete(0, StrBuffPageText.length());
                    }
                }
            } else {
                int intColumnStatus = ExcelFactory.init().getColumnByName(sheetDatatable, "STATUS");
                int intColumnKeterangan = ExcelFactory.init().getColumnByName(sheetDatatable, "KETERANGAN");

                String strHeading = "";
                StringBuffer StrBuffPageText = new StringBuffer();

                if (!MyConfig.boolCreateWPByData) {
                    createSummaryTable(sheetDatatable);
                }

                int intRowCount = sheetDatatable.getLastRowNum() - sheetDatatable.getFirstRowNum();

                for (int j = 1; j <= intRowCount; j++) {
                    try {
                        String tempStrScenario = sheetDatatable.getRow(j).getCell(intColumnScenario).toString();
                        String strScenario = !tempStrScenario.equalsIgnoreCase("") ? tempStrScenario : FilesUtil.init().getDatatableFileName();
                        String strWPName = j + "-" + strScenario;

                        if (strWPName.length() > 40) {
                            strWPName = strWPName.substring(0, 40);
                        }
                        strHeading = "WP-" + strWPName;
                        StrBuffPageText.append(System.getProperty("line.separator"));
                        StrBuffPageText.append("Status: ;" + sheetDatatable.getRow(j).getCell(intColumnStatus).toString());
                        StrBuffPageText.append(System.getProperty("line.separator"));
                        try {
                            StrBuffPageText.append("Keterangan: " + sheetDatatable.getRow(j).getCell(intColumnKeterangan).toString());
                        } catch (NullPointerException n) {
                            StrBuffPageText.append("Keterangan: ");
                        }
                        StrBuffPageText.append(System.getProperty("line.separator"));

                        if (listImageFile.size() != 0) {
                            Boolean boolNoMatch = true;

                            for (int i = 0; i <= intCounterImageFileName; i++) {
                                if (Integer.parseInt(arrStrWPName.get(i).get(0).split("-")[0]) == j) {
                                    addPage(strHeading, StrBuffPageText, strImagePath, listImageFile.get(i), PDType1Font.COURIER, 12);
                                    boolNoMatch = false;
                                }
                            }
                            if (boolNoMatch) {
                                addPage(strHeading, StrBuffPageText, strImagePath, null, PDType1Font.COURIER, 12);
                            }
                        } else {
                            addPage(strHeading, StrBuffPageText, strImagePath, null, PDType1Font.COURIER, 12);
                        }
                        StrBuffPageText.delete(0, StrBuffPageText.length());
                    } catch (NullPointerException e) {
                        StrBuffPageText.delete(0, StrBuffPageText.length());
                    }
                }
            }
            saveThenClose();

            instance = null;

        } catch (Throwable e) {
            e.printStackTrace();
            System.out.println("Failed to create PDF WP Report");
        }
    }

    /**
     * to create Table of Summary of Result Status (PASSED & FAILED)
     * @param sheetReport sheet where the summary table is located
     */
    private void createSummaryTable(Sheet sheetReport) {
        String strHeading = "SUMMARY REPORT TABLE";
        StringBuffer StrBuffPageText = new StringBuffer();
        List<Integer> listLength = new ArrayList();

        try {
            int intColumnSummaryStatus = ExcelFactory.init().getColumnByName(sheetReport, "SUMMARY'S STAT");
            try {

                for (int i = 0; i < 3; i++){
                    int idxLength=0;
                    StrBuffPageText.append(System.getProperty("line.separator"));
                    for (int j = intColumnSummaryStatus; j < sheetReport.getRow(0).getLastCellNum(); j++) {
                        String flag = sheetReport.getRow(i).getCell(j).toString();
                        if (i==0){
                            listLength.add(flag.length());
                        }
                        int centerTextFrnt = 0;
                        int centerTextBehn = 0;
                        if (sheetReport.getRow(i).getCell(j).toString().length()<listLength.get(idxLength) && i!=0){
                            int centerText = Math.round(((float) listLength.get(idxLength) - (float) sheetReport.getRow(i).getCell(j).toString().length()) / 2);
                            if (String.valueOf(((float) listLength.get(idxLength++) - (float) sheetReport.getRow(i).getCell(j).toString().length()) / 2).matches("[0-9]{1,3}[.][1-9]")){
                                centerTextFrnt = centerText;
                                centerTextBehn = centerText-1;
                            } else {
                                centerTextFrnt = centerText;
                                centerTextBehn = centerText;
                            }
                        } else if (i!=0) {
                            idxLength++;
                        }
                        if (j != sheetReport.getRow(0).getLastCellNum()-1) {
                            if (sheetReport.getRow(i).getCell(j).toString().equalsIgnoreCase("PASSED") || sheetReport.getRow(i).getCell(j).toString().equalsIgnoreCase("FAILED")) {
                                StrBuffPageText.append(" ;| ").append(" ".repeat(Math.max(0, centerTextFrnt))).append(sheetReport.getRow(i).getCell(j).toString()).append(" ".repeat(Math.max(0, centerTextBehn)));
                            }
                            else {
                                StrBuffPageText.append(" | ").append(" ".repeat(Math.max(0, centerTextFrnt))).append(sheetReport.getRow(i).getCell(j).toString()).append(" ".repeat(Math.max(0, centerTextBehn)));
                            }
                        } else {
                            StrBuffPageText.append(" | ").append(" ".repeat(Math.max(0, centerTextFrnt))).append(sheetReport.getRow(i).getCell(j).toString()).append(" ".repeat(Math.max(0, centerTextBehn))).append(" | ");
                        }
                    }
                }
            } catch (Exception err){
            }
            addPage(strHeading, StrBuffPageText, "", null, PDType1Font.COURIER, 12);
            StrBuffPageText.delete(0, StrBuffPageText.length());
        } catch (Exception ignored){}
    }

    /**
     * open existing file
     * @param file --> existing PDF Report file
     */
    private void open(File file) {
        try {
            doc.load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * save PDF file to specific path
     */
    private void save() {
        try {
            doc.save(trdStrPath.get() + trdStrWPFileName.get());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * save then close the PDF document
     */
    private void saveThenClose() {
        try {
            save();
        } finally {
            try {
                doc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param strPageHeader -> header title on page
     * @param StrBuffPageText -> sets of information that will printing on page
     * @param strImageDirectory -> directory of images files
     * @param listImageFile -> list of images files
     * @return content creation success status
     */
    private boolean addPage(String strPageHeader, StringBuffer StrBuffPageText, String strImageDirectory, List<String> listImageFile, PDFont font, float fltFontSize) {
        boolean boolContentsStats = false;

        PDPage pdPage = new PDPage();
        doc.addPage(pdPage);
        PDPageContentStream contents = null;

        float fltLeading = 1.0f*fltFontSize;
        PDRectangle pdRecMediabox = pdPage.getMediaBox();

        float margin = 75;
        float width = pdRecMediabox.getWidth() - (2*margin);
        float startX = pdRecMediabox.getLowerLeftX() + margin;
        float startY = pdRecMediabox.getUpperRightY() - margin;
        float yOffset = startY;

        try {
            contents = new PDPageContentStream(doc, pdPage);
            contents.beginText();
            contents.setFont(font, 18);
            contents.newLineAtOffset(startX, startY);
            yOffset-=fltLeading;
            contents.showText(strPageHeader);
            contents.newLineAtOffset(0, -fltLeading);
            yOffset-=fltLeading;

            List<String> lstLines = new ArrayList<>();
            splitIntoIndividualLine(StrBuffPageText, lstLines, fltFontSize, font, width);

            contents.setFont(font, fltFontSize);

            for (String line:lstLines) {
                contents.setNonStrokingColor(Color.black);

                if (line.contains("PASSED")) {
                    String[] splitLine = line.split(";");
                    contents.showText(splitLine[0]);
                    contents.setNonStrokingColor(0f, 0.7f, 0.2f);
                    contents.showText(splitLine[1]);
                } else if (line.contains("FAILED")) {
                    String[] splitLine = line.split(";");
                    contents.showText(splitLine[0]);
                    contents.setNonStrokingColor(1.0f, 0f, 0f);
                    contents.showText(splitLine[1]);
                } else {
                    contents.showText(line);
                }

                contents.newLineAtOffset(0, -fltLeading);
                yOffset-=fltLeading;

                if (yOffset <= 0) {
                    contents.endText();
                    try {
                        if (contents != null) contents.close();
                    } catch (IOException e) {
                        boolContentsStats = false;
                        e.printStackTrace();
                    }
                    pdPage = new PDPage();
                    doc.addPage(pdPage);
                    contents = new PDPageContentStream(doc, pdPage);
                    contents.beginText();
                    contents.setFont(font, fltFontSize);
                    yOffset = startY;
                    contents.newLineAtOffset(startX, startY);
                }
            }

            contents.endText();

            if (listImageFile!=null) {
                float scale = 1f;
                for (String attachmentName : listImageFile) {
                    PDImageXObject pdImage = PDImageXObject.createFromFile(strImageDirectory + attachmentName, doc);
                    scale = width / pdImage.getWidth();
                    yOffset -= (pdImage.getHeight() * scale) + 10;
                    if (yOffset <= margin) {
                        try {
                            if (contents != null) contents.close();
                        } catch (IOException e) {
                            boolContentsStats = false;
                            e.printStackTrace();
                        }
                        pdPage = new PDPage();
                        doc.addPage(pdPage);
                        contents = new PDPageContentStream(doc, pdPage);
                        yOffset = startY - (pdImage.getHeight() * scale);
                    }
                    contents.drawImage(pdImage, startX, yOffset, width, pdImage.getHeight() * scale);
                    contents.setStrokingColor(Color.black);
                    contents.addRect(startX, yOffset, width+2, pdImage.getHeight()*scale+2);
                    contents.closeAndStroke();
                }
            }
            boolContentsStats = true;
        } catch (IOException e) {
            e.printStackTrace();
            boolContentsStats = false;
        } finally {
            try {
                if (contents != null) contents.close();
            } catch (IOException e) {
                boolContentsStats = false;
                e.printStackTrace();
            }
        }

        return boolContentsStats;
    }

    /**
     * @param strBuffWholeLetter --> whole information that will printing on page
     * @param lstLines --> list to be used as the order line of text information
     * @param fltFontSize --> font size
     * @param pdFont --> font type
     * @param width --> the width of content space on the page
     * @throws IOException
     */
    private void splitIntoIndividualLine(StringBuffer strBuffWholeLetter, List<String> lstLines, float fltFontSize, PDFont pdFont, float width) throws IOException {
        String[] paragraphs = strBuffWholeLetter.toString().split(System.getProperty("line.separator"));

        for (int i = 0; i < paragraphs.length; i++) {
            int lastSpace = -1;
            lstLines.add(" ");

            while (paragraphs[i].length() > 0) {
                int spaceIndex = paragraphs[i].indexOf(' ', lastSpace + 1);

                if (spaceIndex < 0) {
                    spaceIndex = paragraphs[i].length();
                }

                String subString = paragraphs[i].substring(0, spaceIndex);
                float size = fltFontSize * pdFont.getStringWidth(subString) / 1000;

                if (size > width) {
                    if (lastSpace < 0) {
                        lastSpace = spaceIndex;
                    }

                    if (paragraphs[i].length()>65) {
                        subString = paragraphs[i].substring(0, 65);
                        lstLines.add(subString);
                        paragraphs[i] = paragraphs[i].substring(65).trim();
                    } else {
                        subString = paragraphs[i].substring(0, lastSpace);
                        lstLines.add(subString);
                        paragraphs[i] = paragraphs[i].substring(lastSpace).trim();
                    }

                    lastSpace = -1;
                } else if (spaceIndex == paragraphs[i].length()) {
                    lstLines.add(paragraphs[i]);
                    paragraphs[i] = "";
                } else {
                    lastSpace = spaceIndex;
                }
            }
        }
    }
}
