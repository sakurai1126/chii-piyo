// 適正範囲（令和5年乳幼児身体発育調査 男子 3〜97パーセンタイル）

type GrowthStandardRange = {
  ageLabel: string;
  // 月齢の下限以上・上限未満
  ageRangeMonths: [number, number];
  heightRange: [number, number];
  weightRange: [number, number];
};

export const growthStandardRanges: GrowthStandardRange[] = [
  {
    ageLabel: "0年1〜2月未満",
    ageRangeMonths: [1, 2],
    heightRange: [51.3, 58.9],
    weightRange: [3.76, 5.8],
  },
  {
    ageLabel: "0年2〜3月未満",
    ageRangeMonths: [2, 3],
    heightRange: [54.9, 62.7],
    weightRange: [4.59, 6.97],
  },
  {
    ageLabel: "0年3〜4月未満",
    ageRangeMonths: [3, 4],
    heightRange: [58, 65.9],
    weightRange: [5.22, 7.84],
  },
  {
    ageLabel: "0年4〜5月未満",
    ageRangeMonths: [4, 5],
    heightRange: [60.2, 68.3],
    weightRange: [5.71, 8.5],
  },
  {
    ageLabel: "0年5〜6月未満",
    ageRangeMonths: [5, 6],
    heightRange: [61.9, 70.1],
    weightRange: [6.09, 9.01],
  },
  {
    ageLabel: "0年6〜7月未満",
    ageRangeMonths: [6, 7],
    heightRange: [63.2, 71.6],
    weightRange: [6.4, 9.41],
  },
  {
    ageLabel: "0年7〜8月未満",
    ageRangeMonths: [7, 8],
    heightRange: [64.4, 72.8],
    weightRange: [6.64, 9.73],
  },
  {
    ageLabel: "0年8〜9月未満",
    ageRangeMonths: [8, 9],
    heightRange: [65.5, 74],
    weightRange: [6.85, 10],
  },
  {
    ageLabel: "0年9〜10月未満",
    ageRangeMonths: [9, 10],
    heightRange: [66.5, 75.2],
    weightRange: [7.03, 10.23],
  },
  {
    ageLabel: "0年10〜11月未満",
    ageRangeMonths: [10, 11],
    heightRange: [67.4, 76.3],
    weightRange: [7.2, 10.45],
  },
  {
    ageLabel: "0年11〜12月未満",
    ageRangeMonths: [11, 12],
    heightRange: [68.4, 77.4],
    weightRange: [7.37, 10.66],
  },
  {
    ageLabel: "1年0〜6月未満",
    ageRangeMonths: [12, 18],
    heightRange: [71.6, 81.1],
    weightRange: [7.94, 11.43],
  },
  {
    ageLabel: "1年6〜12月未満",
    ageRangeMonths: [18, 24],
    heightRange: [76.7, 87.2],
    weightRange: [8.93, 12.84],
  },
  {
    ageLabel: "2年0〜6月未満",
    ageRangeMonths: [24, 30],
    heightRange: [80.5, 91.9],
    weightRange: [9.85, 14.22],
  },
  {
    ageLabel: "2年6〜12月未満",
    ageRangeMonths: [30, 36],
    heightRange: [84.3, 96.6],
    weightRange: [10.66, 15.5],
  },
  {
    ageLabel: "3年0〜6月未満",
    ageRangeMonths: [36, 42],
    heightRange: [87.8, 100.8],
    weightRange: [11.38, 16.7],
  },
  {
    ageLabel: "3年6〜12月未満",
    ageRangeMonths: [42, 48],
    heightRange: [91, 104.7],
    weightRange: [12.1, 17.94],
  },
  {
    ageLabel: "4年0〜6月未満",
    ageRangeMonths: [48, 54],
    heightRange: [94.2, 108.6],
    weightRange: [12.84, 19.27],
  },
  {
    ageLabel: "4年6〜12月未満",
    ageRangeMonths: [54, 60],
    heightRange: [97.3, 112.3],
    weightRange: [13.64, 20.74],
  },
  {
    ageLabel: "5年0〜6月未満",
    ageRangeMonths: [60, 66],
    heightRange: [100.3, 116],
    weightRange: [14.49, 22.35],
  },
  {
    ageLabel: "5年6〜12月未満",
    ageRangeMonths: [66, 72],
    heightRange: [103.3, 119.7],
    weightRange: [15.37, 24.12],
  },
  {
    ageLabel: "6年0〜6月未満",
    ageRangeMonths: [72, 78],
    heightRange: [106.3, 123.2],
    weightRange: [16.28, 26.04],
  },
];
