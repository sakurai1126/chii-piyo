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
  data: { month: string; standardRange?: number[]; value: number | null }[];
  title: string;
  color: string;
  graphHeight: number;
  unit: string;
  chartType: "line" | "bar" | "growth";
};

export const GraphChart = ({ data, title, color, graphHeight, unit, chartType }: Props) => {
  return (
    <div className="border-brown-dark rounded-lg border bg-white/50 px-6 py-5 backdrop-blur-[7.5px] max-md:overflow-hidden max-md:px-0 max-md:py-3">
      <h3 className="mb-4 text-sm max-md:mb-2 max-md:pl-3 max-md:text-xs">{title}</h3>
      <div className="w-full max-md:overflow-x-scroll max-md:px-3">
        <div className="w-full **:outline-none! max-md:min-w-200" style={{ height: graphHeight }}>
          {/* 親要素の大きさに合わせてグラフを自動リサイズ */}
          <ResponsiveContainer width="100%" height="100%">
            {/* ComposedChart: 共通のX軸・Y軸を保ったまま、Line,Bar,Areaなどのグラフを組み合わせて配置可能にする */}
            <ComposedChart data={data} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
              {/* 背景のグリッド線 */}
              <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#CCCCCC" />

              {/* X軸ラベル(月表示) */}
              <XAxis dataKey="month" tick={{ fontSize: 10, fill: "#888888" }} />

              {/* Y軸ラベル */}
              <YAxis domain={["auto", "auto"]} tick={{ fontSize: 10, fill: "#888888" }} />

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
                    ? `${value[0]} ${unit} ~ ${value[1]} ${unit}`
                    : `${value} ${unit}`;
                  return [displayValue, name];
                }}
              />

              {/* 成長曲線の帯の描画 */}
              {chartType === "growth" && (
                <Area
                  type="monotone"
                  // データとしてstandardRange（[min, max]の配列）を渡すことで対象範囲が塗りつぶされる
                  dataKey="standardRange"
                  name="標準範囲"
                  stroke={`${color}4D`}
                  fill={`${color}4D`}
                  fillOpacity={0.3}
                />
              )}

              {/* 棒グラフの描画 */}
              {chartType === "bar" && <Bar dataKey="value" name="記録" fill={color} barSize={20} />}

              {/* 折れ線グラフの描画 */}
              {(chartType === "growth" || chartType === "line") && (
                <Line
                  type="monotone"
                  dataKey="value"
                  name="記録"
                  stroke={color}
                  strokeWidth={3}
                  dot={{ fill: color, strokeWidth: 2 }}
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
