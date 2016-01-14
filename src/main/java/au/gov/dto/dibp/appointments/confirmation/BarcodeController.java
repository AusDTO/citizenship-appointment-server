package au.gov.dto.dibp.appointments.confirmation;

import au.gov.dto.dibp.appointments.client.Client;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
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

    @RequestMapping(value = "/pdf417", method = RequestMethod.GET, produces = IMAGE_PNG)
    public void barcode417(@AuthenticationPrincipal Client client, HttpServletResponse response) throws IOException, WriterException {
        response.setContentType(IMAGE_PNG);
        Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>() {{
            put(EncodeHintType.MARGIN, MARGIN_PIXELS);
            put(EncodeHintType.ERROR_CORRECTION, 2);
        }};
        BitMatrix matrix = new MultiFormatWriter().encode(client.getClientId(), BarcodeFormat.PDF_417, BARCODE_WIDTH, BARCODE_HEIGHT, hints);
        MatrixToImageWriter.writeToStream(matrix, IMAGE_FORMAT, response.getOutputStream());
    }

    @RequestMapping(value = "/code128", method = RequestMethod.GET, produces = IMAGE_PNG)
    public void barcode128(@AuthenticationPrincipal Client client, HttpServletResponse response) throws IOException, WriterException {
        response.setContentType(IMAGE_PNG);
        Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>() {{
            put(EncodeHintType.MARGIN, MARGIN_PIXELS);
        }};
        BitMatrix matrix = new MultiFormatWriter().encode(client.getClientId(), BarcodeFormat.CODE_128, BARCODE_WIDTH, BARCODE_HEIGHT, hints);
        MatrixToImageWriter.writeToStream(matrix, IMAGE_FORMAT, response.getOutputStream());
    }

    @RequestMapping(value = "/qr", method = RequestMethod.GET, produces = IMAGE_PNG)
    public void barcodeQr(@AuthenticationPrincipal Client client, HttpServletResponse response) throws IOException, WriterException {
        response.setContentType(IMAGE_PNG);
        Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>() {{
            put(EncodeHintType.MARGIN, MARGIN_PIXELS);
            put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        }};
        BitMatrix matrix = new MultiFormatWriter().encode(client.getClientId(), BarcodeFormat.QR_CODE, BARCODE_WIDTH, QR_CODE_HEIGHT, hints);
        MatrixToImageWriter.writeToStream(matrix, IMAGE_FORMAT, response.getOutputStream());
    }

    @RequestMapping(value = "/ean13", method = RequestMethod.GET, produces = IMAGE_PNG)
    public void barcode13(@AuthenticationPrincipal Client client, HttpServletResponse response) throws IOException, WriterException {
        response.setContentType(IMAGE_PNG);
        Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>() {{
            put(EncodeHintType.MARGIN, MARGIN_PIXELS);
        }};
        // TODO: 14/01/2016 Implement EAN-13 check digit calculation. This currently works for Client ID 12345678911
        BitMatrix matrix = new MultiFormatWriter().encode("0" + client.getClientId() + "1", BarcodeFormat.EAN_13, BARCODE_WIDTH, BARCODE_HEIGHT, hints);
        MatrixToImageWriter.writeToStream(matrix, IMAGE_FORMAT, response.getOutputStream());
    }
}
