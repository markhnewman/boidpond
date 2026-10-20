#pragma once

#include "Color.h"

Color mixHsv(const Color & zero, const Color & one, double t);
Color voronoiTexture(const Color & hsvMin, const Color & hsvMax, double size, double x, double y);
Color fractalColor(double x, double y, double id);
Color marbleTexture(const Color & fill, const Color & crack, double size, double x, double y);
