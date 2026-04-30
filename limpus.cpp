#include <iostream>
#include <limits>

double add(double a, double b)     { return a + b; }
double subtract(double a, double b){ return a - b; }
double multiply(double a, double b){ return a * b; }
double divide(double a, double b)  { 
    if (b == 0) {
        std::cout << "错误：除数不能为0。\n";
        return std::numeric_limits<double>::quiet_NaN();
    }
    return a / b;
}

int main() {
    double num1, num2;
    char op;
    std::cout << "欢迎使用C++计算器！\n";
    std::cout << "请输入计算表达式（如 3 + 5）：";

    while (true) {
        std::cin >> num1 >> op >> num2;
        if (std::cin.fail()) {
            std::cin.clear(); //清除错误标记
            std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');
            std::cout << "输入格式错误，请重新输入（如 3 + 5）：";
            continue;
        }
        double result;

        switch (op) {
            case '+': result = add(num1, num2); break;
            case '-': result = subtract(num1, num2); break;
            case '*': result = multiply(num1, num2); break;
            case '/': result = divide(num1, num2); break;
            default:
                std::cout << "不支持的操作符，请输入 +, -, *, /：";
                continue;
        }
        std::cout << "结果：" << result << std::endl;
        std::cout << "请输入下一个表达式，或按 Ctrl+C 退出：";
    }
    return 0;
}
