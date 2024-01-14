package git.eclipse.core.graphics.font;

/**
 * Creates a font Glyph.
 *
 * @param width   Width of the Glyph
 * @param height  Height of the Glyph
 * @param x       X coordinate on the font texture
 * @param y       Y coordinate on the font texture
 * @param advance Advance width
 */
public record Glyph(int width, int height, int x, int y, float advance) {

}
