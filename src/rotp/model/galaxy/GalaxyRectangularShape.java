/*
 * Copyright 2015-2020 Ray Fowler
 * 
 * Licensed under the GNU General Public License, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     https://www.gnu.org/licenses/gpl-3.0.html
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rotp.model.galaxy;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import rotp.model.game.IGameOptions;

public class GalaxyRectangularShape extends GalaxyShape {
    // modnar: add options for StarField
    public static final List<String> options1;
    public static final List<String> options2;
    private static final long serialVersionUID = 1L;
    static {
        options1 = new ArrayList<>();
        options1.add("SETUP_RECTANGLE_0");
        options1.add("SETUP_RECTANGLE_1");
        options2 = new ArrayList<>();
        options2.add("SETUP_VOID_0");
        options2.add("SETUP_VOID_1");
        options2.add("SETUP_VOID_2");
        options2.add("SETUP_VOID_5");
    }
	
    Shape block, circle;
	Area totalArea, blockArea, circleArea;
	float adjust_density = 0.8f; // modnar: adjust stellar density
    float rectangleRatio = 4.0f/3.0f;
    int voids = 0;
	
    public GalaxyRectangularShape(IGameOptions options) {
        opts = options;
    }
    @Override
    public List<String> options1()  { return options1; }
    @Override
    public List<String> options2()  { return options2; }
    @Override
    public String defaultOption1()  { return options1.get(0); }
    @Override
    public String defaultOption2()  { return options2.get(0); }
    @Override
    public float maxScaleAdj()               { return 0.95f; }
    @Override
    public void init(int n) {
        super.init(n);

        int option1 = max(0, options1.indexOf(opts.selectedGalaxyShapeOption1()));
        int option2 = max(0, options2.indexOf(opts.selectedGalaxyShapeOption2()));
        
        switch(option1) {
            case 0: {
                rectangleRatio = 4.0f/3.0f;
                adjust_density = 0.8f;
                break;
            }
            case 1: {
                rectangleRatio = 4.0f/3.0f;
                adjust_density = 1.6f;
                break;
            }
            default: rectangleRatio = 4.0f/3.0f; break;
        }
        
        // reset w/h vars since aspect ratio may have changed
        initWidthHeight();
        
        float gE = (float) galaxyEdgeBuffer();
        float gW = (float) galaxyWidthLY();
        float gH = (float) galaxyHeightLY();
        
        block = new Rectangle2D.Float(gE, gE, gW, gH);
		blockArea = new Area(block);
		totalArea = blockArea;
		
		// modnar: choose void configurations with option2
        switch(option2) {
            case 0: break;
            case 1: {
                // single large central void
                circle = new Ellipse2D.Float(gE+0.5f*gW-0.44f*gH, gE+0.06f*gH, 0.88f*gH, 0.88f*gH);
                circleArea = new Area(circle);
                totalArea.subtract(circleArea);
                adjust_density *= 1.8f;
                break;
            }
            case 2: {
                // two diagonal voids
                circle = new Ellipse2D.Float(gE+0.05f*gW, gE+0.05f*gH, 0.45f*gW, 0.45f*gW);
                circleArea = new Area(circle);
                totalArea.subtract(circleArea);
                
                circle = new Ellipse2D.Float(gE+0.5f*gW, gE+0.95f*gH-0.45f*gW, 0.45f*gW, 0.45f*gW);
                circleArea = new Area(circle);
                totalArea.subtract(circleArea);
                adjust_density *= 1.8f;
                break;
            }
            case 3: {
                // five separated voids
                circle = new Ellipse2D.Float(gE+0.26f*gW, gE+0.5f*gH-0.24f*gW, 0.48f*gW, 0.48f*gW);
                circleArea = new Area(circle);
                totalArea.subtract(circleArea);
                
                circle = new Ellipse2D.Float(gE+0.05f*gW, gE+0.067f*gH, 0.3f*gH, 0.3f*gH);
                circleArea = new Area(circle);
                totalArea.subtract(circleArea);
                
                circle = new Ellipse2D.Float(gE+0.05f*gW, gE+0.633f*gH, 0.3f*gH, 0.3f*gH);
                circleArea = new Area(circle);
                totalArea.subtract(circleArea);
                
                circle = new Ellipse2D.Float(gE+0.95f*gW-0.3f*gH, gE+0.067f*gH, 0.3f*gH, 0.3f*gH);
                circleArea = new Area(circle);
                totalArea.subtract(circleArea);
                
                circle = new Ellipse2D.Float(gE+0.95f*gW-0.3f*gH, gE+0.633f*gH, 0.3f*gH, 0.3f*gH);
                circleArea = new Area(circle);
                totalArea.subtract(circleArea);
                adjust_density *= 1.8f;
                break;
            }
            default: break;
        }
    }
    @Override
    protected int galaxyWidthLY() { 
        return (int) (Math.sqrt(adjust_density*rectangleRatio*opts.numberStarSystems()*adjustedSizeFactor()));
    }
    @Override
    protected int galaxyHeightLY() { 
        return (int) (Math.sqrt(adjust_density*(1/rectangleRatio)*opts.numberStarSystems()*adjustedSizeFactor()));
    }
    @Override
    public void setRandom(Point.Float pt) {
        pt.x = randomLocation(width, galaxyEdgeBuffer());
        pt.y = randomLocation(height, galaxyEdgeBuffer());
    }
    @Override
    public boolean valid(float x, float y) {
        float buff = galaxyEdgeBuffer();
        if (x > (width-buff))
            return false;
        if (x < buff)
            return false;
        if (y > (height-buff))
            return false;
        if (y < buff)
            return false;
        return totalArea.contains(x, y);
    }
    float randomLocation(float max, float buff) {
        return buff + (random() * (max-buff-buff));
    }
    @Override
    protected float sizeFactor(String size) {
        switch (opts.selectedGalaxySize()) {
            case IGameOptions.SIZE_TINY:      return 10; 
            case IGameOptions.SIZE_SMALL:     return 15; 
            case IGameOptions.SIZE_SMALL2:    return 17;
            case IGameOptions.SIZE_MEDIUM:    return 19; 
            case IGameOptions.SIZE_MEDIUM2:   return 20; 
            case IGameOptions.SIZE_LARGE:     return 21; 
            case IGameOptions.SIZE_LARGE2:    return 22; 
            case IGameOptions.SIZE_HUGE:      return 23; 
            case IGameOptions.SIZE_HUGE2:     return 24; 
            case IGameOptions.SIZE_MASSIVE:   return 25; 
            case IGameOptions.SIZE_MASSIVE2:  return 26; 
            case IGameOptions.SIZE_MASSIVE3:  return 27; 
            case IGameOptions.SIZE_MASSIVE4:  return 28; 
            case IGameOptions.SIZE_MASSIVE5:  return 29; 
            case IGameOptions.SIZE_INSANE:    return 32; 
            case IGameOptions.SIZE_LUDICROUS: return 36; 
            default:             return 19; 
        }
    }

}
