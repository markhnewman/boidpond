#include "WriteBMP.h"

#include <fstream>

using namespace std;

namespace {
    void writeUint32(ofstream & out, uint32_t data) {
        for (int i = 0; i < 4; ++i) {
            unsigned char c = data & 0xFF;
            out.write((char *)&c, 1);
            data = data >> 8;
        }
    }

    inline unsigned char doubleToUchar(double d) {
        return floor(255.0 * fmin(fmax(d, 0.0), 1.0) + 0.5);
    }
}

void WriteBMP::writeRGB(string filename, int width, int height, vector<double> rgb) {
    ofstream out;
    out.open(filename.c_str(), std::ios::binary);

    int padsize = width % 4;
    int filesize = 54 + height * (3 * width + padsize);

    out.write("BM", 2);             // File format identifier for Windows
    writeUint32(out, filesize);     // File size in bytes
    writeUint32(out, 0);            // Reserved
    writeUint32(out, 54);           // Offset to beginning of bitmap data in bytes
    writeUint32(out, 40);           // Size of InfoHeader
    writeUint32(out, width);        // Bitmap width in pixels
    writeUint32(out, height);       // Bitmap height in pixels
    writeUint32(out, 0x00180001);   // Planes = 1, Bits per pixel = 24 (16 bits each)
    writeUint32(out, 0);            // Compression
    writeUint32(out, 0);            // Size of compressed image
    writeUint32(out, 2000);         // Horizontal resolution in pixels/meter
    writeUint32(out, 2000);         // Vertical resolution in pixels/meter
    writeUint32(out, 0);            // Number of colors in the color palette
    writeUint32(out, 0);            // Number of important colors

    vector<unsigned char> data;
    int i = 0;
    for (int y = 0; y < height; ++y) {
        for (int x = 0; x < width; ++x) {
            data.push_back(doubleToUchar(rgb[i + 2]));
            data.push_back(doubleToUchar(rgb[i + 1]));
            data.push_back(doubleToUchar(rgb[i + 0]));
            i += 3;
        }
        for (int x = 0; x < padsize; ++x)
            data.push_back(0);
    }

    out.write((char *)data.data(), data.size());
    out.close();
}

void WriteBMP::writeRGBA(string filename, int width, int height, vector<double> rgba) {
    ofstream out;
    out.open(filename.c_str(), std::ios::binary);

    int imagesize = 4 * height * width;
    int filesize = 70 + imagesize;

    out.write("BM", 2);             // File format identifier for Windows
    writeUint32(out, filesize);     // File size in bytes
    writeUint32(out, 0);            // Reserved
    writeUint32(out, 70);           // Offset to beginning of bitmap data in bytes
    writeUint32(out, 56);           // Size of InfoHeader
    writeUint32(out, width);        // Bitmap width in pixels
    writeUint32(out, height);       // Bitmap height in pixels
    writeUint32(out, 0x00200001);   // Planes = 1, Bits per pixel = 32 (16 bits each)
    writeUint32(out, 3);            // Compression = BI_BITFIELDS
    writeUint32(out, imagesize);    // Size of compressed image
    writeUint32(out, 2000);         // Horizontal resolution in pixels/meter
    writeUint32(out, 2000);         // Vertical resolution in pixels/meter
    writeUint32(out, 0);            // Number of colors in the color palette
    writeUint32(out, 0);            // Number of important colors
    writeUint32(out, 0xFF000000);   // Red
    writeUint32(out, 0x00FF0000);   // Green
    writeUint32(out, 0x0000FF00);   // Blue
    writeUint32(out, 0x000000FF);   // Fourth color???

    vector<unsigned char> data;
    for (int i = 0; i < height * width; ++i) {
        data.push_back(doubleToUchar(rgba[4 * i + 3]));
        data.push_back(doubleToUchar(rgba[4 * i + 2]));
        data.push_back(doubleToUchar(rgba[4 * i + 1]));
        data.push_back(doubleToUchar(rgba[4 * i + 0]));
    }

    out.write((char *)data.data(), data.size());
    out.close();
}
