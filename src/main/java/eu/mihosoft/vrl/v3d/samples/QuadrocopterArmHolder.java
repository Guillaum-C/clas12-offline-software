/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.v3d.samples;

import eu.mihosoft.vrl.v3d.CSG;
import eu.mihosoft.vrl.v3d.Cube;
import eu.mihosoft.vrl.v3d.Cylinder;
import eu.mihosoft.vrl.v3d.FileUtil;
import static eu.mihosoft.vrl.v3d.Transform.unity;
import eu.mihosoft.vrl.v3d.Vector3d;
import java.io.IOException;
import java.nio.file.Paths;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class QuadrocopterArmHolder {

    public CSG toCSG(double armHeight, double armScaleFactor, double armCubeWidth, double armCubeThickness, double holderPlatformRadius, double holderPlatformThickness) {

        double tol = 2;
        double holderWallThickness = 3;
        double armOverlap = 10;

        double holderCubeWidth = tol + holderWallThickness * 2 + armCubeWidth;
        double holderCubeHeight = armHeight + holderWallThickness;
        double holderCubeDepth = armOverlap + armCubeThickness + holderWallThickness;

        CSG holderCube = new Cube(holderCubeWidth, holderCubeDepth, holderCubeHeight).toCSG();

        double armWidth = armHeight * armScaleFactor;

        CSG armCube = new Cube(armCubeWidth + tol, armCubeThickness, armHeight).
                toCSG().transformed(unity().translateY(-armCubeThickness));
        CSG arm = new Cube(armWidth, holderCubeDepth, armHeight).toCSG().
                transformed(unity().translateY(armCubeThickness).translateZ(armHeight / 2.0));
        arm = new Cylinder(armHeight / 2.0, holderCubeDepth - holderWallThickness, 32).
                toCSG().transformed(unity().rotX(90).
                        translate(0, 0, -holderWallThickness).scaleX(armScaleFactor)).union(arm);
        CSG holder = holderCube.difference(armCube.union(arm).
                transformed(unity().translate(0, 0, holderWallThickness)));

//        return holder;

        CSG holderPlatform = new Cylinder(holderPlatformRadius, holderPlatformThickness, 64).toCSG().transformed(unity().scaleY(1.25).translateY(-holderPlatformRadius/2.0));
      
        return holderPlatform.union(holder.transformed(unity().translateZ(4*holderWallThickness))).transformed(unity().rotZ(-90));
    }

    public static void main(String[] args) throws IOException {
        CSG result = new QuadrocopterArmHolder().toCSG(18, 0.5, 18, 4, 36, 3);

        FileUtil.write(Paths.get("quadrocopter-arm-holder.stl"), result.toStlString());
        result.toObj().toFiles(Paths.get("quadrocopter-arm-holder.obj"));
    }
}
