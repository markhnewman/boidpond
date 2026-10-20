#include "Color.h"

#include <math.h>

Color operator+(const Color & lhs, const Color & rhs) {
    Color color;
    for (int i = 0; i < 3; ++i)
        color[i] = lhs[i] + rhs[i];
    return color;
}

Color operator-(const Color & lhs, const Color & rhs) {
    Color color;
    for (int i = 0; i < 3; ++i)
        color[i] = lhs[i] - rhs[i];
    return color;
}

Color operator*(double lhs, const Color & rhs) {
    Color color;
    for (int i = 0; i < 3; ++i)
        color[i] = lhs * rhs[i];
    return color;
}

Color operator*(const Color & lhs, double rhs) {
    Color color;
    for (int i = 0; i < 3; ++i)
        color[i] = lhs[i] * rhs;
    return color;
}

Color operator/(const Color & lhs, double rhs) {
    Color color;
    for (int i = 0; i < 3; ++i)
        color[i] = lhs[i] / rhs;
    return color;
}

double clamp(double x) {
    return fmin(fmax(x, 0.0), 1.0);
}

Color hsvColor(const Color & hsv) {
    double h = fmod(hsv[0], 6.0);
    double r = clamp(fabs(h - 3.0) - 1.0);
    double g = clamp(2.0 - fabs(h - 2.0));
    double b = clamp(2.0 - fabs(h - 4.0));
    return hsv[2] * (hsv[1] * Color(r, g, b) + (1.0 - hsv[1]) * Color(1.0));
}

Color mixColor(const Color & zero, const Color & one, double t) {
    return (1.0 - t) * zero + t * one;
}
