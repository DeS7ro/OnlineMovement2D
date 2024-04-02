package client;

import java.awt.*;
import java.util.StringTokenizer;

public record UI(Graphics2D g2D)
{
    private static final String DELIMITER = "#";
    public static final String WHITE = DELIMITER + "%255,255,255" + DELIMITER;
    public static final String BLACK = DELIMITER + "%0,0,0" + DELIMITER;
    public static final String RED = DELIMITER + "%255,0,0" + DELIMITER;
    public static final String BLUE = DELIMITER + "%0,0,255" + DELIMITER;
    public static final String GREEN = DELIMITER + "%0,255,0" + DELIMITER;
    public static final String BOLD = DELIMITER + "%bold" + DELIMITER;
    public static final String PLAIN = DELIMITER + "%plain" + DELIMITER;

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

    public void drawString(String string, int x, int y)
    {
        if (!string.contains(DELIMITER + "%"))
        {
            g2D.drawString(string, x, y);
            return;
        }

        FontMetrics fontMetrics = g2D().getFontMetrics();
        StringTokenizer st = new StringTokenizer(string, "#");


        for (int i = 0; i < st.countTokens(); i++)
        {
            String next = st.nextToken();
            if (next.contains("%"))
            {
                String[] split = next.split("%");

                if (split[1].contains(","))
                {
                    //TODO: COLORE
                }
                else
                {
                    if (split[1].equals("bold"))
                    {
                        setFontSize(Font.BOLD);
                    }
                    else if (split[1].equals("plain"))
                    {
                        setFontSize(Font.PLAIN);
                    }

                }

                g2D.drawString(next, x, y);
                x += fontMetrics.stringWidth(next);

            }
            else
            {
                g2D.drawString(next, x, y);
                x += fontMetrics.stringWidth(next);
            }
        }


    }
}
