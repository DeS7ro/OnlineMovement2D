package client;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public record UI(Graphics2D g2D)
{
    public static final String BLACK = "\033[0;30m";   // BLACK
    public static final String RED = "\033[0;31m";     // RED
    public static final String GREEN = "\033[0;32m";   // GREEN
    public static final String YELLOW = "\033[0;33m";  // YELLOW
    public static final String BLUE = "\033[0;34m";    // BLUE
    public static final String PURPLE = "\033[0;35m";  // PURPLE
    public static final String CYAN = "\033[0;36m";    // CYAN
    public static final String WHITE = "\033[0;37m";   // WHITE

    public static final String BLACK_BOLD = "\033[1;30m";  // BLACK
    public static final String RED_BOLD = "\033[1;31m";    // RED
    public static final String GREEN_BOLD = "\033[1;32m";  // GREEN
    public static final String YELLOW_BOLD = "\033[1;33m"; // YELLOW
    public static final String BLUE_BOLD = "\033[1;34m";   // BLUE
    public static final String PURPLE_BOLD = "\033[1;35m"; // PURPLE
    public static final String CYAN_BOLD = "\033[1;36m";   // CYAN
    public static final String WHITE_BOLD = "\033[1;37m";  // WHITE

    public static final String BLACK_ITALIC = "\033[2;30m";  // BLACK
    public static final String RED_ITALIC = "\033[2;31m";    // RED
    public static final String GREEN_ITALIC = "\033[2;32m";  // GREEN
    public static final String YELLOW_ITALIC = "\033[2;33m"; // YELLOW
    public static final String BLUE_ITALIC = "\033[2;34m";   // BLUE
    public static final String PURPLE_ITALIC = "\033[2;35m"; // PURPLE
    public static final String CYAN_ITALIC = "\033[2;36m";   // CYAN
    public static final String WHITE_ITALIC = "\033[2;37m";  // WHITE

    public static final String RESET = "\033[0m";  // Text Reset

    public UI(Graphics2D g2D)
    {
        this.g2D = g2D;

        g2D.setFont(new Font("Arial", Font.PLAIN, 20));

        g2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2D.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2D.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2D.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }

    public void drawDarkened(Rectangle rectangle, float alpha)
    {
        g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2D.fill(rectangle);
        g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }

    public void setFontSize(int style, float size)
    {
        g2D.setFont(g2D.getFont().deriveFont(style, size));
    }

    public void setFontSize(int style)
    {
        g2D.setFont(g2D.getFont().deriveFont(style));
    }

    public void setFontSize(float size)
    {
        g2D.setFont(g2D.getFont().deriveFont(size));
    }

    public void drawString(String text, int x, int y)
    {
        Map<String, Color> ANSI_COLOR_MAP = new HashMap<>();
        Map<String, Integer> ANSI_STYLE_MAP = new HashMap<>();

        ANSI_COLOR_MAP.put("30", Color.BLACK);
        ANSI_COLOR_MAP.put("31", Color.RED);
        ANSI_COLOR_MAP.put("32", new Color(55, 200, 15));
        ANSI_COLOR_MAP.put("33", Color.YELLOW);
        ANSI_COLOR_MAP.put("34", Color.BLUE);
        ANSI_COLOR_MAP.put("35", new Color(128, 0, 128)); // Purple
        ANSI_COLOR_MAP.put("36", Color.CYAN);
        ANSI_COLOR_MAP.put("37", Color.WHITE);

        ANSI_STYLE_MAP.put("0", Font.PLAIN);
        ANSI_STYLE_MAP.put("1", Font.BOLD);
        ANSI_STYLE_MAP.put("2", Font.ITALIC);

        for (int i = 0; i < text.length(); i++)
        {
            char c = text.charAt(i);
            if (c == '\033')
            { // Check for ANSI escape sequence
                int endIndex = text.indexOf('m', i);
                int startIndex = text.indexOf(';', i);
                if (endIndex != -1)
                {
                    if (startIndex != -1)
                    {
                        String colorCode = text.substring(startIndex+1, endIndex);
                        if (ANSI_COLOR_MAP.containsKey(colorCode)) {
                            g2D.setColor(ANSI_COLOR_MAP.get(colorCode));
                        }

                        String styleCode = text.substring(startIndex - 1, startIndex);
                        if (ANSI_STYLE_MAP.containsKey(styleCode)) {
                            setFontSize(ANSI_STYLE_MAP.get(styleCode));
                        }

                    }
                    else
                    {
                        g2D.setColor(Color.BLACK); // Reset color to black
                        setFontSize(Font.PLAIN);
                    }

                    i = endIndex; // Skip the ANSI escape sequence

                }
            }
            else
            {
                g2D.drawChars(new char[]{c}, 0, 1, x, y);
                x += g2D.getFontMetrics().charWidth(c);
            }
        }
    }
}
