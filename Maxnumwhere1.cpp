#include<iostream>
using namespace std;
const long long MAXN=10001;
int main(){
	long long a[MAXN];
	int i,n,maxa,k;
	cin>>n;
	for(i=1;i<=n;i++) cin>>a[i];
	maxa=a[i];
	k=i;
	for(i=2;i<=n;i++) 
	if(a[i]>maxa)
	{
		maxa=a[i];
		k=i;
	}
	cout<<k;
	return 0;
}
