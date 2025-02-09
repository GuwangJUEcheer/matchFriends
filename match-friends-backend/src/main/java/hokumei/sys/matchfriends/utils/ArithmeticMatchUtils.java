package hokumei.sys.matchfriends.utils;

import java.util.List;

/**
 * 最小编辑距离
 */
public class ArithmeticMatchUtils {
	public static int minEditDistance(List<String> list1, List<String> list2) {
		int m = list1.size();
		int n = list2.size();

		// DP数组
		int[][] dp = new int[m + 1][n + 1];

		// 初始化边界条件
		for (int i = 0; i <= m; i++) {
			dp[i][0] = i; // 需要删除 i 个元素
		}
		for (int j = 0; j <= n; j++) {
			dp[0][j] = j; // 需要插入 j 个元素
		}

		// 动态规划填表
		for (int i = 1; i <= m; i++) {
			for (int j = 1; j <= n; j++) {
				if (list1.get(i - 1).equals(list2.get(j - 1))) {
					dp[i][j] = dp[i - 1][j - 1]; // 相等，无需操作
				} else {
					dp[i][j] = Math.min(
							Math.min(dp[i - 1][j], dp[i][j - 1]),  // 插入 or 删除
							dp[i - 1][j - 1]                        // 替换
					) + 1;
				}
			}
		}

		// 结果：两个列表之间的最小编辑距离
		return dp[m][n];
	}
}
