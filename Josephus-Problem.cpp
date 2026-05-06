#include <iostream>
#include <vector>
using namespace std;

int main() {
    int n, m;
    cin >> n >> m;
    
    vector<bool> alive(n + 1, true); // 记录每个人是否还在圈中
    int current = 0; // 当前位置
    int out_count = 0; // 已出圈人数
    
    while (out_count < n) {
        int count = 0;
        do {
            current = current % n + 1; // 循环到下一个人
            if (alive[current]) {
                count++;
            }
        } while (count < m);
        
        // 输出出圈的人
        cout << current;
        if (out_count < n - 1) cout << " ";
        
        alive[current] = false; // 标记为已出圈
        out_count++;
    }
    
    cout << endl;
    return 0;
}
