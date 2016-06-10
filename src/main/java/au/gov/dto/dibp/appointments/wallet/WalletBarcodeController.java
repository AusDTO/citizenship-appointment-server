package au.gov.dto.dibp.appointments.wallet;

import au.gov.dto.dibp.appointments.client.Client;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Controller
class WalletBarcodeController {
    private static final String IMAGE_PNG = "image/png";
    private static final int SIZE_PIXELS = 250;

    private final String passTypeIdentifier;

    @Autowired
    public WalletBarcodeController(@Value("${wallet.pass.type.identifier}") String passTypeIdentifier) {
        this.passTypeIdentifier = passTypeIdentifier;
    }

    @RequestMapping(value = "/wallet/pass/barcode.png", method = RequestMethod.GET, produces = IMAGE_PNG)
    public void passBarcode(@AuthenticationPrincipal Client client,
                            HttpServletRequest request,
                            HttpServletResponse response) throws WriterException, IOException, URISyntaxException {
        response.setContentType("image/png");
        String passUrlPath = String.format("/wallet/v1/passes/%s/citizenship?id=%s&otherid=%s", passTypeIdentifier, client.getClientId(), client.getCustomerId());
        String passUrl = new URI(request.getRequestURL().toString()).resolve(passUrlPath).toASCIIString();
        BitMatrix bitMatrix = new MultiFormatWriter().encode(passUrl, BarcodeFormat.QR_CODE, SIZE_PIXELS, SIZE_PIXELS);
        BufferedImage barcodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        ImageIO.write(barcodeImage, "PNG", response.getOutputStream());
    }
}
