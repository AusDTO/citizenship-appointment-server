package au.gov.dto.dibp.appointments.confirmation;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.pdf417.encoder.Compaction;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/barcode")
public class BarcodeController {
    private static final String IMAGE_PNG = "image/png";
    private static final String IMAGE_FORMAT = "png";
    private static final int BARCODE_WIDTH = 250;
    private static final int BARCODE_HEIGHT = 75;
    private static final int QR_CODE_HEIGHT = BARCODE_WIDTH;
    private static final int MARGIN_PIXELS = 10;

    @RequestMapping(value = "/pdf417/{id}", method = RequestMethod.GET, produces = IMAGE_PNG)
    public void barcode417(@PathVariable("id") String id, HttpServletResponse response) throws IOException, WriterException {
        response.setContentType(IMAGE_PNG);
        Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>() {{
            put(EncodeHintType.MARGIN, MARGIN_PIXELS);
            put(EncodeHintType.ERROR_CORRECTION, 2);
            put(EncodeHintType.PDF417_COMPACT, true);
            put(EncodeHintType.PDF417_COMPACTION, Compaction.TEXT);
        }};
        BitMatrix matrix = new MultiFormatWriter().encode(id, BarcodeFormat.PDF_417, BARCODE_WIDTH, BARCODE_HEIGHT, hints);
        MatrixToImageWriter.writeToStream(matrix, IMAGE_FORMAT, response.getOutputStream());
    }

    @RequestMapping(value = "/code128/{id}", method = RequestMethod.GET, produces = IMAGE_PNG)
    public void barcode128(@PathVariable("id") String id, HttpServletResponse response) throws IOException, WriterException {
        response.setContentType(IMAGE_PNG);
        Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>() {{
            put(EncodeHintType.MARGIN, MARGIN_PIXELS);
        }};
        BitMatrix matrix = new MultiFormatWriter().encode(id, BarcodeFormat.CODE_128, BARCODE_WIDTH, BARCODE_HEIGHT, hints);
        MatrixToImageWriter.writeToStream(matrix, IMAGE_FORMAT, response.getOutputStream());
    }

    @RequestMapping(value = "/qr/{id}", method = RequestMethod.GET, produces = IMAGE_PNG)
    public void barcodeQr(@PathVariable("id") String id, HttpServletResponse response) throws IOException, WriterException {
        response.setContentType(IMAGE_PNG);
        Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>() {{
            put(EncodeHintType.MARGIN, MARGIN_PIXELS);
            put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        }};
        BitMatrix matrix = new MultiFormatWriter().encode(id, BarcodeFormat.QR_CODE, BARCODE_WIDTH, QR_CODE_HEIGHT, hints);
        MatrixToImageWriter.writeToStream(matrix, IMAGE_FORMAT, response.getOutputStream());
    }
}
