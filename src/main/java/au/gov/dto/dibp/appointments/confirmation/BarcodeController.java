package au.gov.dto.dibp.appointments.confirmation;

import au.gov.dto.dibp.appointments.util.InputValidationException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.pdf417.encoder.Compaction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Controller
@RequestMapping(value = "/barcode")
public class BarcodeController {
    private static final Pattern CLIENT_ID_PATTERN = Pattern.compile("^[0-9]{11}$");

    private static final String IMAGE_PNG = "image/png";
    private static final int MARGIN_PIXELS = 10;
    private static final int BARCODE_WIDTH = 250;
    private static final int BARCODE_HEIGHT = 75;
    private static final int CROPPED_WIDTH = 158;
    private static final int CROPPED_HEIGHT = 84;

    @RequestMapping(value = "/pdf417/{id}", method = RequestMethod.GET, produces = IMAGE_PNG)
    public void barcode417(@PathVariable("id") String id, HttpServletResponse response) throws IOException, WriterException {
        if (!CLIENT_ID_PATTERN.matcher(id).matches()) {
            throw new InputValidationException("Invalid clientId for barcode [" + id + "]");
        }
        response.setContentType(IMAGE_PNG);
        Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>() {{
            put(EncodeHintType.MARGIN, MARGIN_PIXELS);
            put(EncodeHintType.ERROR_CORRECTION, 2);
            put(EncodeHintType.PDF417_COMPACT, true);
            put(EncodeHintType.PDF417_COMPACTION, Compaction.TEXT);
        }};
        BitMatrix matrix = new MultiFormatWriter().encode(id, BarcodeFormat.PDF_417, BARCODE_WIDTH, BARCODE_HEIGHT, hints);
        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(matrix);
        BufferedImage croppedImage = cropImageWorkaroundDueToZxingBug(bufferedImage);
        ImageIO.write(croppedImage, "PNG", response.getOutputStream());
    }

    /**
     * zxing currently doesn't crop the image correctly when PDF417 compaction is applied.
     * Instead, it leaves the right portion of the image blank (white).
     * This code, with its magic numbers, crops the image to get rid of this white area.
     */
    private BufferedImage cropImageWorkaroundDueToZxingBug(BufferedImage bufferedImage) {
        return bufferedImage.getSubimage(0, 0, CROPPED_WIDTH, CROPPED_HEIGHT);
    }
}
