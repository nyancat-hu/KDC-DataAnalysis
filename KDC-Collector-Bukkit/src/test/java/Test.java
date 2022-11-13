import java.util.*;

public class Test {
    static HashSet<String> hashSet = new HashSet<>();
    static LinkedList<Integer> mark = new LinkedList<>();
    public static void main(String[] args) {
        List<String> wordDict = new LinkedList<>();
        Collections.addAll(wordDict, "leet","code");
        lengthOfLIS(new int[]{10,9,2,5,3,7,101,18});
    }
    public static int lengthOfLIS(int[] nums) {
        int[][] dp = new int[nums.length][nums.length];
        for(int j=1;j<nums.length;j++){
            for(int i=0;i<j;i++){
                if(nums[j]>nums[j-1]) dp[i][j] = dp[i][j-1] +1;
            }
        }

        int maxVal = 0;
        for(int i =0;i<nums.length;i++) if(dp[i][nums.length-1]>maxVal) maxVal=dp[i][nums.length-1];
        return maxVal;
    }
}
