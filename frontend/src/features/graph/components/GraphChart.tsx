"use client";

import {
  ResponsiveContainer,
  ComposedChart,
  Line,
  Bar,
  Area,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
} from "recharts";

type Props = {
  data: {
    month?: string;
    day?: string;
    standardRange?: number[];
    value: number | null;
    secondValue?: number | null;
  }[];

  variant: "height" | "weight" | "milk" | "diaper" | "word";
};

export const GraphChart = ({ data, variant }: Props) => {
  const dataKey = variant === "milk" || variant === "diaper" ? "day" : "month";

  const displayInfo = {
    height: { title: "身長", unit: "cm", graphHeight: 400 },
    weight: { title: "体重", unit: "kg", graphHeight: 400 },
    milk: { title: "ミルク量", unit: "ml", graphHeight: 300 },
    diaper: { title: "排泄記録", unit: "回", graphHeight: 300 },
    word: { title: "覚えた言葉", unit: "語", graphHeight: 300 },
  };
  return (
    <div className="border-brown-dark rounded-lg border bg-white/50 px-6 py-5 backdrop-blur-[7.5px] max-md:overflow-hidden max-md:px-0 max-md:py-3">
      <h3 className="mb-4 text-sm max-md:mb-2 max-md:pl-3 max-md:text-xs">
        {displayInfo[variant].title}
      </h3>
      <div className="w-full max-md:overflow-x-scroll max-md:px-3">
        <div
          className="w-full **:outline-none! max-md:min-w-200"
          style={{ height: displayInfo[variant].graphHeight }}
        >
          {/* 親要素の大きさに合わせてグラフを自動リサイズ */}
          <ResponsiveContainer width="100%" height="100%">
            {/* ComposedChart: 共通のX軸・Y軸を保ったまま、Line,Bar,Areaなどのグラフを組み合わせて配置可能にする */}
            <ComposedChart data={data} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
              {/* 背景のグリッド線 */}
              <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#CCCCCC" />

              {/* X軸ラベル(月表示) */}
              <XAxis dataKey={dataKey} tick={{ fontSize: 10, fill: "#888888" }} />

              {/* Y軸ラベル */}
              <YAxis
                // ミルク量と排泄回数のみ下限を0に指定
                domain={variant === "milk" || variant === "diaper" ? [0, "auto"] : ["auto", "auto"]}
                tick={{ fontSize: 10, fill: "#888888" }}
                // 排泄記録と言葉の数の場合は小数を許可しない
                allowDecimals={variant === "diaper" || variant === "word" ? false : true}
              />

              {/* データにホバーした際に出る吹き出し */}
              <Tooltip
                // 枠のデザイン
                contentStyle={{
                  borderRadius: "8px",
                  border: "none",
                  boxShadow: "0 4px 20px rgba(0,0,0,0.08)",
                }}
                // 各項目の文字色
                labelStyle={{ color: "#888888" }}
                itemStyle={{ color: "#333333" }}
                // 値の表示形式をカスタマイズ
                formatter={(value, name) => {
                  const displayValue = Array.isArray(value)
                    ? `${value[0]} ${displayInfo[variant].unit} ~ ${value[1]} ${displayInfo[variant].unit}`
                    : `${value} ${displayInfo[variant].unit}`;
                  return [displayValue, name];
                }}
              />

              {variant === "height" && (
                <>
                  {/* 成長曲線の帯の描画 */}
                  <Area
                    type="monotone"
                    // データとしてstandardRange（[min, max]の配列）を渡すことで対象範囲が塗りつぶされる
                    dataKey="standardRange"
                    name="標準範囲"
                    stroke="#FF4F4F4D"
                    fill="#FF4F4F4D"
                    fillOpacity={0.3}
                  />

                  {/* 折れ線グラフの描画 */}
                  <Line
                    type="monotone"
                    dataKey="value"
                    name="記録"
                    stroke="#FF4F4F"
                    strokeWidth={3}
                    dot={{ fill: "#FF4F4F", strokeWidth: 2 }}
                    // 途中の月に記録がない場合でも、前後の記録同士を線で繋ぐ設定
                    connectNulls={true}
                  />
                </>
              )}

              {variant === "weight" && (
                <>
                  {/* 成長曲線の帯の描画 */}
                  <Area
                    type="monotone"
                    // データとしてstandardRange（[min, max]の配列）を渡すことで対象範囲が塗りつぶされる
                    dataKey="standardRange"
                    name="標準範囲"
                    stroke="#D1CB324D"
                    fill="#D1CB324D"
                    fillOpacity={0.3}
                  />

                  {/* 折れ線グラフの描画 */}
                  <Line
                    type="monotone"
                    dataKey="value"
                    name="記録"
                    stroke="#D1CB32"
                    strokeWidth={3}
                    dot={{ fill: "#D1CB32", strokeWidth: 2 }}
                    // 途中の月に記録がない場合でも、前後の記録同士を線で繋ぐ設定
                    connectNulls={true}
                  />
                </>
              )}

              {/* ミルク量棒グラフの描画 */}
              {variant === "milk" && (
                <Bar dataKey="value" name="記録" fill="#4ADB26" barSize={20} />
              )}

              {/* 排泄記録棒グラフの描画 */}
              {variant === "diaper" && (
                <>
                  <Bar dataKey="value" name="おしっこ" fill="#26B5DB" barSize={12} />
                  <Bar dataKey="secondValue" name="うんち" fill="#026F8C" barSize={12} />
                </>
              )}

              {/* 覚えた言葉の数折れ線グラフの描画 */}
              {variant === "word" && (
                <Line
                  type="monotone"
                  dataKey="value"
                  name="記録"
                  stroke="#DB5926"
                  strokeWidth={3}
                  dot={{ fill: "#DB5926", strokeWidth: 2 }}
                  // 途中の月に記録がない場合でも、前後の記録同士を線で繋ぐ設定
                  connectNulls={true}
                />
              )}
            </ComposedChart>
          </ResponsiveContainer>
        </div>
      </div>
    </div>
  );
};
