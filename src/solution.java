public class solution {
    /*Write a Program to find the second-highest number in an array.

        int arr[] = { ,14, 46, 47, 94, 94, 52, 86, 36, 94, 89 };
    * */
    public int secondHighest(int[] arr){
        int result = Integer.MIN_VALUE;
        int max =Integer.MIN_VALUE;
        for(int i =0; i<arr.length; i++){
            if(arr[i]>max){
                result = max;
                max = arr[i];
            }else{
                if(arr[i] != max && arr[i]>result) result = arr[i];
            }

        }

        return result;
    }

    public static void main(String[] args){
       // System.out.println("Hello, World!");
        int arr[] = { 14, 46, 47, 52, 86, 36, 89 };
        solution s = new solution();
        int result = s.secondHighest(arr);
        System.out.println("2nd highest: "+result);
    }
}
