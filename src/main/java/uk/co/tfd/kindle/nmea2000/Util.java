package uk.co.tfd.kindle.nmea2000;

import com.amazon.kindle.booklet.AbstractBooklet;
import com.amazon.kindle.booklet.BookletContext;
import com.amazon.kindle.restricted.content.catalog.ContentCatalog;
import com.amazon.kindle.restricted.runtime.Framework;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.font.TextAttribute;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Util {

    private static final Logger log = LoggerFactory.getLogger(Util.class);
    private static boolean kindle;
    public static int DEFAULT_SCREEN_RESOLUTION = 111;
    private static int screenResolution = DEFAULT_SCREEN_RESOLUTION; // default for awt on OSX.

	public static final int KINDLE_FRAME_WIDTH = 1072;
	public static final int KINDLE_FRAME_HEIGHT = 1390;


	public static int scaleKindle(int xy) {
		if ( kindle ) {
			return xy;
		} else {
			return (int)((535.0/1072.0)*xy);
		}
	}

	public static Font createFont(float sz) {
		if (isKindle()) {
			sz = sz*0.53f;
		}
		Map<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
		attributes.put(TextAttribute.FAMILY, "Arial");
		attributes.put(TextAttribute.SIZE, sz);
		return new Font(attributes);
	}
	public static Font createExtraBoldFont(float sz) {
		if (isKindle()) {
			sz = sz*0.53f;
		}
		Map<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
		attributes.put(TextAttribute.FAMILY, "Arial");
		attributes.put(TextAttribute.SIZE, sz);
		attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_EXTRABOLD);
		return new Font(attributes);
	}

	public static int descaleKindle(int xy) {
		if ( kindle ) {
			return xy;
		} else {
			return (int)( ((1072.0/535.0)) * xy);
		}
	}

	public static BookletContext obGetBookletContext(int j, AbstractBooklet booklet){
		BookletContext bc = null;
		Method[] methods = AbstractBooklet.class.getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getReturnType() == BookletContext.class) {
				// Double check that it takes no arguments, too...
                System.err.println("OOOOOOOOOOOOOOOOOOOOOOOOOOO");
				Class[] params = methods[i].getParameterTypes();
                System.err.println("OOOOOOOOOOOOOOOOOOOOOOOOOOO");
				if (params.length == 0) {
					try {
                        System.err.println(i);
                        System.err.println(methods[i]);
                        System.err.println(methods[i].getReturnType().getName());
                        System.err.println(methods[i].getName());
                        System.err.println(methods[i].getParameterCount());
                        System.err.println(booklet);
                        System.err.println("---------------------------------------");
						bc = (BookletContext) methods[i].invoke(booklet, (Object[])null);
                        System.err.println("OOOOOOOOOOOOOOOOOOOOOOOOOOO");
                        System.err.println(bc);
                    } catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
                        e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
                        e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
                        e.printStackTrace();
                    }
					break;
				}
			}
		}
		System.err.println("OOOOOOOOOOOOOOOOOOOOOOOOOOO");
        return bc;
	}

	public static Container getUIContainer(AbstractBooklet booklet) throws InvocationTargetException, IllegalAccessException {

		Method getUIContainer = null;
        System.err.println("OOOOOOOOOOOOOOOOOOOOOOOOOOO");
        // Should be the only method returning a Container in BookletContext...
		Method[] methods = BookletContext.class.getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getReturnType() == Container.class) {
				// Double check that it takes no arguments, too...
				Class[] params = methods[i].getParameterTypes();
				if (params.length == 0) {
					getUIContainer = methods[i];
                    System.err.println("OOOOOOOOOOOOOOOOOOOOOOOOOOO");
                    break;
				}
			}
		}


		if (getUIContainer != null) {

            System.err.println("OOOOOOOOOOOOOOOOOOOOOOOOOOO");
            //new Logger().append("Found getUIContainer method as " + getUIContainer.toString());
			BookletContext bc = Util.obGetBookletContext(1, booklet);
            System.err.println("OOOOOOOOOOOOOOOOOOOOOOOOOOO");
            Container rootContainer = (Container) getUIContainer.invoke(bc, (Object[])null);
            System.err.println("OOOOOOOOOOOOOOOOOOOOOOOOOOO");
            return rootContainer;
		}
		else {
			return null;
		}
	}

	// And this was always obfuscated...
	// NOTE: Pilfered from KPVBooklet (https://github.com/koreader/kpvbooklet/blob/master/src/com/github/chrox/kpvbooklet/ccadapter/CCAdapter.java)
	/**
	 * Perform CC request of type "query" and "change"
	 * @param req_type request type of "query" or "change"
	 * @param req_json request json string
	 * @return return json object
	 */
	private static JSONObject ccPerform(String req_type, String req_json) {
		ContentCatalog CC = (ContentCatalog) Framework.getService(ContentCatalog.class);
		try {
			Method perform = null;

			// Enumeration approach
			Class[] signature = {String.class, String.class, int.class, int.class};
			Method[] methods = ContentCatalog.class.getDeclaredMethods();
			for (int i = 0; i < methods.length; i++) {
				Class[] params = methods[i].getParameterTypes();
				if (params.length == signature.length) {
					int j;
					for (j = 0; j < signature.length && params[j].isAssignableFrom( signature[j] ); j++ ) {}
					if (j == signature.length) {
						perform = methods[i];
						break;
					}
				}
			}

			if (perform != null) {
				JSONObject json = (JSONObject) perform.invoke(CC, new Object[] { req_type, req_json, new Integer(200), new Integer(5) });
				return json;
			}
			else {
				System.err.println("Failed to find perform method, last access time won't be set on exit!");
				return new JSONObject();
			}
		} catch (Throwable t) {
			throw new RuntimeException(t.toString());
		}
	}


	public static void updateCCDB(String tag, String path) {
		long lastAccess = new Date().getTime() / 1000L;
		path = JSONObject.escape(path);
		// NOTE: Hard-code the path, as no-one should be using a custom .kual trigger...
		String json_query = "{\"filter\":{\"Equals\":{\"value\":\"" + path + "\",\"path\":\"location\"}},\"type\":\"QueryRequest\",\"maxResults\":1,\"sortOrder\":[{\"order\":\"descending\",\"path\":\"lastAccess\"},{\"order\":\"ascending\",\"path\":\"titles[0].collation\"}],\"startIndex\":0,\"id\":1,\"resultType\":\"fast\"}";
		JSONObject json = Util.ccPerform("query", json_query);
		JSONArray values = (JSONArray) json.get("values");
		JSONObject value = (JSONObject) values.get(0);
		String uuid = (String) value.get("uuid");
		String json_change = "{\"commands\":[{\"update\":{\"uuid\":\"" + uuid + "\",\"lastAccess\":" + lastAccess + ",\"displayTags\":[\"" + tag + "\"]" + "}}],\"type\":\"ChangeRequest\",\"id\":1}";
		Util.ccPerform("change", json_change);
		//new Logger().append("Set KUAL's lastAccess ccdb entry to " + lastAccess);
	}

	public static <T> T option(Object v, T defaultValue) {
		if ( v == null ) {
			return defaultValue;
		}
		return (T) v;
	}

	public static <T> T option(Map<String, Object> map, String key, T defaultValue) {
		if ( map != null && map.containsKey(key)) {
			return (T) map.get(key);
		}
		return defaultValue;
	}
	public static <T> T required(Map<String, Object> map, String key) {
		if ( map != null && map.containsKey(key)) {
			return (T) map.get(key);
		}
		throw new IllegalArgumentException("Option "+key+" is required");
	}

    public static <T> T resolve(Map<String, Object> input, String path, T defaultValue) {
        String[] elements = path.split("\\.");
        Object o = input;
        for (int i = 0; i < elements.length-1; i++) {
			if ( elements[i].length() > 0) {
				if ( o instanceof Map) {
					Map<Object, String> m = (Map<Object, String>) o;
					o = m.get(elements[i]);
				} else {
					return defaultValue;
				}
			}
        }
        if ( o instanceof Map) {
            Map<Object, String> m = (Map<Object, String>) o;
			o = m.get(elements[elements.length - 1]);
            if ( o == null ) {
                return defaultValue;
            } else if (defaultValue == null) {
                return (T) o;
            } else if ( defaultValue.getClass().isAssignableFrom(o.getClass())) {
				return (T) o;
			} else if ( defaultValue instanceof String ) {
				return (T) String.valueOf(o);
			} else if ( defaultValue instanceof Long ) {
				return (T) Long.valueOf(String.valueOf(o));
			} else if ( defaultValue instanceof  Double ) {
				return (T) Double.valueOf(String.valueOf(o));
			} else {
				throw new IllegalArgumentException("Unable to resolve "+o.getClass()+" to "+defaultValue.getClass()+" from "+path);
			}
        } else {
            return defaultValue;
        }
    }

    public static boolean isKindle() {
        return kindle;
    }



    public static void setKindle(boolean kindleValue) {
        kindle = kindleValue;
    }

    public static void setScreenResolution(int screenResolution) {
        Util.screenResolution = screenResolution;
    }

    public static int getScreenResolution() {
        return screenResolution;
    }


	public static int intValue(Object v, int i) {
		if ( v instanceof Long ) {
			return ((Long)v).intValue();
		} else if ( v instanceof  Integer) {
			return ((Integer)v).intValue();
		} else if ( v instanceof  Double) {
			return ((Double)v).intValue();
		} else if ( v instanceof  String) {
			return Integer.parseInt((String) v);
		} else {
			return i;
		}
	}
	public static long longValue(Object v, long i) {
		if ( v instanceof Long ) {
			return ((Long)v).longValue();
		} else if ( v instanceof  Integer) {
			return ((Integer)v).longValue();
		} else if ( v instanceof  Double) {
			return ((Double)v).longValue();
		} else if ( v instanceof  String) {
			return Long.parseLong((String)v);
		} else {
			return i;
		}
	}
	public static double doubleValue(Object v, double i) {
		if ( v instanceof Long ) {
			return ((Long)v).doubleValue();
		} else if ( v instanceof  Integer) {
			return ((Integer)v).doubleValue();
		} else if ( v instanceof  Double) {
			return ((Double)v).doubleValue();
		} else if ( v instanceof  String) {
			return Double.parseDouble((String)v);
		} else {
			return i;
		}
	}

	/**
	 * Note using this will block nav on the parent frame.s
	 * @param component
	 */
	public static void addMouseTracker(JPanel component) {
		if ( !kindle ) {
			final JLabel mousePosition = new JLabel("0,0");
			component.add(mousePosition);
			Dimension s = mousePosition.getPreferredSize();
			mousePosition.setBounds(scaleKindle(10), scaleKindle(KINDLE_FRAME_HEIGHT) - 50, 100, 50);

			component.addMouseMotionListener(new MouseMotionListener() {
				@Override
				public void mouseDragged(MouseEvent e) {
				}

				@Override
				public void mouseMoved(MouseEvent e) {
					mousePosition.setText(String.format("%d, %d", descaleKindle(e.getX()), descaleKindle(e.getY())));
				}
			});
		}
	}



	/**
     * Created by ieb on 09/06/2020.
     */
    public enum HAlign {
        RIGHT, CENTER, LEFT, DECIMAL
    }

    public enum VAlign {
        BOTTOM, TOP, BASELINE, CENTER;
    }


    public static void drawString(String s, int x, int y, Font font, HAlign halign, VAlign valign, Graphics2D g2) {
        if (s != null) {

            g2.setFont(font);
            FontMetrics fontMetrics = g2.getFontMetrics();
            int width = fontMetrics.stringWidth(s);
            Rectangle2D r = font.getStringBounds(s, g2.getFontRenderContext());

            switch (halign) {
                case CENTER:
                    x = x - (int) width/2;
                    break;
                case RIGHT:
                    x = x - (int) width;
                    break;
				case DECIMAL:
					if (s.indexOf('.') > 0) {
						width = fontMetrics.stringWidth(s);
					}
					x = x - (int) width;
					break;
            }
            switch (valign) {
                case CENTER:
                    y = y + (int) fontMetrics.getAscent()/2;
                    break;
                case TOP:
                    y = y + (int) fontMetrics.getAscent();
                    break;
                case BOTTOM:
                    y = y - fontMetrics.getDescent();
            }


            g2.clearRect(x, y - (int) fontMetrics.getAscent(),(int)r.getWidth(),(int)r.getHeight());
            g2.drawString(s, x, y);
        }

    }

	public static void drawStringRotated(String text, int x, int y, Font font, Graphics2D g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.rotate(-Math.PI / 2.0);
		FontMetrics fontMetrics = g2.getFontMetrics();
		Rectangle2D r = font.getStringBounds(text, g2.getFontRenderContext());
		x = x - (int) (r.getWidth()/2.0);
		y = y + (int) (fontMetrics.getAscent()/2.0);
		g2.setClip((int)(-r.getWidth()/2.0), (int)(-r.getHeight()/2.0), (int)r.getWidth(), (int)r.getHeight());
		g2.clearRect(x, y - fontMetrics.getAscent(),(int)r.getWidth(),(int)r.getHeight());
		g2.drawString(text, x, y);

		g2.rotate(Math.PI / 2.0);
	}


	// kindle boundary
	public static void drawGrid(Graphics g) {
		if ( !kindle) {
			g.drawRect(0, 0, scaleKindle(KINDLE_FRAME_WIDTH), scaleKindle(KINDLE_FRAME_HEIGHT));
			for (int i = 0; i < KINDLE_FRAME_HEIGHT; i += 100) {
				g.drawLine(0, scaleKindle(i), scaleKindle(KINDLE_FRAME_WIDTH), scaleKindle(i));
			}
			for (int i = 0; i < KINDLE_FRAME_WIDTH; i += 100) {
				g.drawLine(scaleKindle(i), 0, scaleKindle(i), scaleKindle(KINDLE_FRAME_HEIGHT));
			}
			g.setColor(Color.RED);
			g.drawLine(scaleKindle(KINDLE_FRAME_WIDTH / 2), 0, scaleKindle(KINDLE_FRAME_WIDTH / 2), scaleKindle(KINDLE_FRAME_HEIGHT));
			g.drawLine(0, scaleKindle(KINDLE_FRAME_HEIGHT / 2), KINDLE_FRAME_WIDTH, scaleKindle(Util.KINDLE_FRAME_HEIGHT / 2));
			g.setColor(Color.BLACK);
		}
	}

	public static void testFontSizes(Graphics g) {
		for (int i = 8; i < 40; i++) {
			g.setFont(Util.createFont((1.0f*i)));
			g.drawString(String.format("F%d", i), scaleKindle(100), scaleKindle(100+(i-8)*40));
		}
	}



}
