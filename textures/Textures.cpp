#include "Textures.h"

#include <math.h>

using namespace std;

namespace {
    Color hashColor(const Color & color) {
        double x = fmod(color[0] * 0.1031, 1.0);
        double y = fmod(color[1] * 0.1030, 1.0);
        double z = fmod(color[2] * 0.0973, 1.0);
        double dot = x * (y + 33.33) + y * (x + 33.33) + z * (z + 33.33);
        x += dot; y += dot; z += dot;
        return Color(fmod((x + y) * z, 1.0), fmod((x + z) * y, 1.0), fmod((z + y) * x, 1.0));
    }

    Color rndColor(const Color & hsvMin, const Color & hsvMax, const Color & hash) {
        Color h = hashColor(hash);
        Color hsv;
        for (int i = 0; i < 3; ++i)
            hsv[i] = hsvMin[i] + h[i] * (hsvMax[i] - hsvMin[i]);
        return hsvColor(hsv);
    }

    Color noiseColor(double x, double y, double id) {
        double idX = floor(x);
        double idY = floor(y);
        x -= idX;
        y -= idY;
        double a = x * x * (3.0 - 2.0 * x);
        double b = y * y * (3.0 - 2.0 * y);
        return mixColor(mixColor(hashColor(Color(idX + 0.0, idY + 0.0, id)),
                                    hashColor(Color(idX + 1.0, idY + 0.0, id)), a),
                        mixColor(hashColor(Color(idX + 0.0, idY + 1.0, id)),
                                    hashColor(Color(idX + 1.0, idY + 1.0, id)), a), b);
    }
}

Color mixHsv(const Color & zero, const Color & one, double t) {
    return hsvColor(mixColor(zero, one, t));
}

Color voronoiTexture(const Color & hsvMin, const Color & hsvMax, double size, double x, double y) {
    double cellX, cellY;
    x = modf((x + 1.0) / size, &cellX);
    y = modf((y + 1.0) / size, &cellY);

    double closestX = cellX;
    double closestY = cellY;
    double closest = 2.0;
    for (int i = 0; i < 3; ++i) {
        for (int j = 0; j < 3; ++j) {
            double cx = cellX + i;
            double cy = cellY + j;
            Color h = hashColor(Color(cx, cy, size + 1.0));
            double dx = h[0] - x + i - 1;
            double dy = h[1] - y + j - 1;
            double distance = sqrt(dx * dx + dy * dy);
            if (distance < closest) {
                closestX = cx;
                closestY = cy;
                closest = distance;
            }
        }
    }

    return rndColor(hsvMin, hsvMax, Color(closestX, closestY, size));
}

Color fractalColor(double x, double y, double id) {
    Color c(0.0);
    double scale = 0.5;
    for (int i = 0; i < 8; ++i) {
        c = c + scale * noiseColor(x, y, id);
        scale *= 0.5;
        double z = 1.6 * x - 1.2 * y;
        y = 1.2 * x + 1.6 * y;
        x = z;
    }
    return c;
}

Color marbleTexture(const Color & fill, const Color & crack, double size, double x, double y) {
    double t = fractalColor(size * x, size * y, 1.0)[0];
    t = 7.0 * abs(t - 0.5);
    t = fmin(fmax(t, 0.0), 1.0);
//    t = t * t * (3.0 - 2.0 * t);
    t = sqrt(1-(1-t)*(1-t));
    return mixColor(crack, fill, t);
}
