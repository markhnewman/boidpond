#pragma once

#include <string>
#include <vector>

namespace WriteBMP {
    void writeRGB(std::string filename, int width, int height, std::vector<double> rgb);
    void writeRGBA(std::string filename, int width, int height, std::vector<double> rgba);
}