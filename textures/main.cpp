#include "Color.h"
#include "Textures.h"
#include "WriteBMP.h"

#include <iostream>
#include <vector>

using namespace std;

const int width = 256;
const int height = 256;

Color sample(double x, double y, double & alpha) {
    double dx = x - 0.5;
    double dy = y - 0.5;
    double r = 2.0 * sqrt(dx * dx + dy * dy);
    alpha = clamp(5.0 * (1.0 - r));
    return voronoiTexture(Color(0.5, 0.0, 1.0), Color(0.5, 1.0, 1.0), 0.1, x, y);
}

Color generate(int x, int y, double & alpha) {
    Color color;
    alpha = 0.0;
    for (int i = 0; i < 4; ++i) {
        for (int j = 0; j < 4; ++j) {
            double a;
            color = color + sample((x + 0.25 * i) / (double) width, (y + 0.25 * j) / (double) height, a);
            alpha += a;
        }
    }
    color = color / 16.0;
    alpha = alpha / 16.0;
    return color;
}

int main(int argc, char * argv[]) {
    if (argc != 2) {
        cout << "usage: " << argv[0] << " output.png" << endl;
        return 0;
    }

    vector<double> data;
    for (int y = 0; y < height; ++y) {
        for (int x = 0; x < width; ++x) {
            double alpha;
            Color color = generate(x, y, alpha);

            data.push_back(color[0]);
            data.push_back(color[1]);
            data.push_back(color[2]);
            data.push_back(alpha);
        }
    }

    WriteBMP::writeRGBA(argv[1], width, height, data);

    return 0;
}
