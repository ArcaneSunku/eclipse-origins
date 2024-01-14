package git.eclipse.core.graphics.font;

import git.eclipse.core.graphics.Texture;
import git.eclipse.core.graphics.render2D.SpriteBatch;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static java.awt.Font.MONOSPACED;
import static java.awt.Font.PLAIN;

public class TTFont {
    private final Map<Character, Glyph> m_Glyphs;
    private final Texture m_Texture;
    private int m_FontHeight;

    public TTFont() {
        this(new Font(MONOSPACED, PLAIN, 16), true);
    }

    public TTFont(boolean antiAlias) {
        this(new Font(MONOSPACED, PLAIN, 16), antiAlias);
    }

    public TTFont(int size) {
        this(size, true);
    }

    public TTFont(int size, boolean antiAlias) {
        this(new Font(MONOSPACED, PLAIN, size), antiAlias);
    }

    public TTFont(String filePath) {
        this(filePath, 16);
    }

    public TTFont(String filePath, int size) {
        this(filePath, size, true);
    }

    public TTFont(String filePath, int size, boolean antiAlias) {
        this(TTFont.class.getResourceAsStream(filePath), size, antiAlias);
    }

    public TTFont(InputStream in, int size) {
        this(in, size, true);
    }

    public TTFont(InputStream in, int size, boolean antiAlias) {
        Font font;
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, in).deriveFont(PLAIN, size);
        } catch (IOException | FontFormatException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException("Failed to load font!");
        }

        m_Glyphs = new HashMap<>();
        m_Texture = createFontTexture(font, antiAlias);
    }

    public TTFont(Font font) {
        this(font, true);
    }

    public TTFont(Font font, boolean antiAlias){
        m_Glyphs = new HashMap<>();
        m_Texture = createFontTexture(font, antiAlias);
    }

    private Texture createFontTexture(Font font, boolean antiAlias) {
        /* Loop through the characters to get charWidth and charHeight */
        int imageWidth = 0;
        int imageHeight = 0;

        /* Start at char #32, because ASCII 0 to 31 are just control codes */
        for (int i = 32; i < 256; i++) {
            if (i == 127) {
                /* ASCII 127 is the DEL control code, so we can skip it */
                continue;
            }
            char c = (char) i;
            BufferedImage ch = createCharImage(font, c, antiAlias);
            if (ch == null) {
                /* If char image is null that font does not contain the char */
                continue;
            }

            imageWidth += ch.getWidth();
            imageHeight = Math.max(imageHeight, ch.getHeight());
        }

        m_FontHeight = imageHeight;

        /* Image for the texture */
        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        int x = 0;

        /* Create image for the standard chars, again we omit ASCII 0 to 31
         * because they are just control codes */
        for (int i = 32; i < 256; i++) {
            if (i == 127) {
                /* ASCII 127 is the DEL control code, so we can skip it */
                continue;
            }
            char c = (char) i;
            BufferedImage charImage = createCharImage(font, c, antiAlias);
            if (charImage == null) {
                /* If char image is null that font does not contain the char */
                continue;
            }

            int charWidth = charImage.getWidth();
            int charHeight = charImage.getHeight();

            /* Create glyph and draw char on image */
            Glyph ch = new Glyph(charWidth, charHeight, x, image.getHeight() - charHeight, 0f);
            g.drawImage(charImage, x, 0, null);
            x += ch.width();
            m_Glyphs.put(c, ch);
        }

        /* Flip image Horizontal to get the origin to bottom left */
        AffineTransform transform = AffineTransform.getScaleInstance(1f, -1f);
        transform.translate(0, -image.getHeight());
        AffineTransformOp operation = new AffineTransformOp(transform,
                AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        image = operation.filter(image, null);

        /* Get charWidth and charHeight of image */
        int width = image.getWidth();
        int height = image.getHeight();

        /* Get pixel data of image */
        int[] pixels = new int[width * height];
        image.getRGB(0, 0, width, height, pixels, 0, width);

        /* Put pixel data into a ByteBuffer */
        ByteBuffer buffer = MemoryUtil.memAlloc(width * height * 4);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                /* Pixel as RGBA: 0xAARRGGBB */
                int pixel = pixels[i * width + j];
                /* Red component 0xAARRGGBB >> 16 = 0x0000AARR */
                buffer.put((byte) ((pixel >> 16) & 0xFF));
                /* Green component 0xAARRGGBB >> 8 = 0x00AARRGG */
                buffer.put((byte) ((pixel >> 8) & 0xFF));
                /* Blue component 0xAARRGGBB >> 0 = 0xAARRGGBB */
                buffer.put((byte) (pixel & 0xFF));
                /* Alpha component 0xAARRGGBB >> 24 = 0x000000AA */
                buffer.put((byte) ((pixel >> 24) & 0xFF));
            }
        }
        /* Do not forget to flip the buffer! */
        buffer.flip();

        /* Create texture */
        Texture fontTexture = new Texture(width, height, buffer);
        MemoryUtil.memFree(buffer);
        return fontTexture;
    }

    private BufferedImage createCharImage(Font font, char c, boolean antiAlias) {
        /* Creating temporary image to extract character size */
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        if (antiAlias) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        g.setFont(font);
        FontMetrics metrics = g.getFontMetrics();
        g.dispose();

        /* Get char charWidth and charHeight */
        int charWidth = metrics.charWidth(c);
        int charHeight = metrics.getHeight();

        /* Check if charWidth is 0 */
        if (charWidth == 0) {
            return null;
        }

        /* Create image for holding the char */
        image = new BufferedImage(charWidth, charHeight, BufferedImage.TYPE_INT_ARGB);
        g = image.createGraphics();
        if (antiAlias) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        g.setFont(font);
        g.setPaint(java.awt.Color.WHITE);
        g.drawString(String.valueOf(c), 0, metrics.getAscent());
        g.dispose();
        return image;
    }

    public void drawText(SpriteBatch batch, CharSequence text, float x, float y, Vector3f color) {
        int textHeight = getHeight(text);
        float drawX = x, drawY = y;

        if (textHeight > m_FontHeight)
            drawY += textHeight - m_FontHeight;

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == '\n') {
                drawY -= m_FontHeight;
                drawX = x;
                continue;
            }

            if (ch == '\r')
                continue;

            Glyph g = m_Glyphs.get(ch);
            batch.render(m_Texture, new Vector2f(g.x(), g.y()), new Vector2f(g.width(), -g.height()), new Vector3f(drawX, drawY, 1.0f), new Vector2f(g.width(), g.height()), color, 1.0f);
            drawX += g.width() + g.advance();
        }
    }

    public void drawText(SpriteBatch batch, CharSequence text, float x, float y) {
        drawText(batch, text, x, y, new Vector3f(1.0f));
    }

    public void dispose() {
        m_Texture.dispose();
    }

    public int getWidth(CharSequence text) {
        int width = 0;
        int lineWidth = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\n') {
                /* Line end, set width to maximum from line width and stored
                 * width */
                width = Math.max(width, lineWidth);
                lineWidth = 0;
                continue;
            }
            if (c == '\r') {
                /* Carriage return, just skip it */
                continue;
            }
            Glyph g = m_Glyphs.get(c);
            lineWidth += g.width();
        }
        width = Math.max(width, lineWidth);
        return width;
    }

    public int getHeight(CharSequence text) {
        int height = 0;
        int lineHeight = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\n') {
                /* Line end, add line height to stored height */
                height += lineHeight;
                lineHeight = 0;
                continue;
            }
            if (c == '\r') {
                /* Carriage return, just skip it */
                continue;
            }
            Glyph g = m_Glyphs.get(c);
            lineHeight = Math.max(lineHeight, g.height());
        }
        height += lineHeight;
        return height;
    }

}
