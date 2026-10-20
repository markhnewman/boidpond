#pragma once

class Color {
public:
    Color() { data[0] = data[1] = data[2] = 0.0; }
    Color(double gray) { data[0] = data[1] = data[2] = gray; }
    Color(double r, double g, double b) { data[0] = r; data[1] = g; data[2] = b; }

    double & operator[](int i) { return data[i]; }
    double operator[](int i) const { return data[i]; }

    double data[3];
};

Color operator+(const Color & lhs, const Color & rhs);
Color operator-(const Color & lhs, const Color & rhs);
Color operator*(double lhs, const Color & rhs);
Color operator*(const Color & lhs, double rhs);
Color operator/(const Color & lhs, double rhs);

double clamp(double x);
Color hsvColor(const Color & hsv);
Color mixColor(const Color & zero, const Color & one, double t);
