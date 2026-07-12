import {
  CareRecordListResponseDto,
  GrowthRecordResponseDto,
  WordRecordResponseDto,
} from "@/lib/api-client/gen";
import { formatJapaneseDateNonTime } from "@/utils/date";

type Props = {
  isAdmin: boolean;
  isEasy: boolean;
  growthRecords: GrowthRecordResponseDto[];
  careRecords: CareRecordListResponseDto;
  wordRecords: WordRecordResponseDto[];
};

export const GraphSummary = ({
  isAdmin,
  isEasy,
  growthRecords,
  careRecords,
  wordRecords,
}: Props) => {
  return (
    <div
      className={`mt-10 grid gap-3 @max-md:mt-6 @max-md:gap-1.5 ${isAdmin ? "grid-cols-11 @max-md:grid-cols-2" : "grid-cols-6 @max-md:grid-cols-2"}`}
    >
      <GrowthGraphSummary growthRecords={growthRecords} />
      {isAdmin && !isEasy && (
        <>
          <DiaperGraphSummary careRecords={careRecords} />
          <MilkGraphSummary careRecords={careRecords} />
        </>
      )}

      <WordGraphSummary isAdmin={isAdmin} isEasy={isEasy} wordRecords={wordRecords} />
    </div>
  );
};

// 前月比計算
const calculateDiff = (
  growthRecords: GrowthRecordResponseDto[],
  type: "height" | "weight",
  latestRecord?: GrowthRecordResponseDto,
) => {
  if (!latestRecord) return null;

  // latestRecordから最新の身長/体重を取得
  const latestValue = type === "height" ? latestRecord.height : latestRecord.weight;
  if (latestValue == null) return null;

  // 最新のデータの記録日時
  const latestDate = new Date(latestRecord.measurementDate);
  // 最新データの1ヶ月前
  const prevMonth = new Date(latestDate.getFullYear(), latestDate.getMonth() - 1);

  // 記録が前月のデータに一致するものを取得
  const prevMonthRecord = growthRecords.find((record) => {
    // 種別ごとにデータを確認
    const value = type === "height" ? record.height : record.weight;
    if (value == null) return false;

    // 記録日時が対象(前月)と一致するか
    return (
      record.measurementDate.getFullYear() === prevMonth.getFullYear() &&
      record.measurementDate.getMonth() === prevMonth.getMonth()
    );
  });

  // 種別ごとに前月データを取得
  const prevValue = type === "height" ? prevMonthRecord?.height : prevMonthRecord?.weight;
  if (prevValue == null) {
    return null;
  }

  // 最新データ(latestValue)から前月データ(prevValue)を引いた値を返す
  return (latestValue - prevValue).toFixed(1);
};

// 前月比の表示テキストを生成
const formatDiffText = (diff: string | null, unit: string) => {
  if (diff == null) return "前月記録なし";
  const sign = Number(diff) > 0 ? "+" : "";
  return `前月比 ${sign}${diff}${unit}`;
};

// 成長記録を計算
const GrowthGraphSummary = ({ growthRecords }: { growthRecords: GrowthRecordResponseDto[] }) => {
  // 最新の身長データを取得
  const latestHeightRecord = growthRecords.find((record) => record.height != null);
  // 最新の体重データを取得
  const latestWeightRecord = growthRecords.find((record) => record.weight != null);

  // 差分表示処理
  const diffHeight = calculateDiff(growthRecords, "height", latestHeightRecord);
  const diffWeight = calculateDiff(growthRecords, "weight", latestWeightRecord);

  return (
    <>
      {/* 身長 */}
      <div className="border-graph-border-height bg-translucent col-span-2 flex h-40 flex-col items-center justify-center rounded-lg border text-center backdrop-blur-[7.5px] @max-md:col-span-1 @max-md:h-30">
        <p className="@max-md:text-[13px]">身長</p>
        <div className="mt-2 flex items-end gap-1 @max-md:mt-1">
          <p className="text-3xl font-medium @max-lg:text-2xl @max-md:text-[28px]">
            {latestHeightRecord?.height ? latestHeightRecord.height.toFixed(1) : "--"}
          </p>
          <p className="text-2xl @max-lg:text-xl">cm</p>
        </div>
        {latestHeightRecord && (
          <p className="text-note-gray mt-2 text-xs @max-md:mt-1">
            {formatJapaneseDateNonTime(latestHeightRecord.measurementDate)}時点
            <br />
            {formatDiffText(diffHeight, "cm")}
          </p>
        )}
      </div>

      {/* 体重 */}
      <div className="border-graph-border-weight bg-translucent col-span-2 flex h-40 flex-col items-center justify-center rounded-lg border text-center backdrop-blur-[7.5px] @max-md:col-span-1 @max-md:h-30">
        <p className="@max-md:text-[13px]">体重</p>
        <div className="mt-2 flex items-end gap-1 @max-md:mt-1">
          <p className="text-3xl font-medium @max-lg:text-2xl @max-md:text-[28px]">
            {latestWeightRecord?.weight ? latestWeightRecord.weight.toFixed(1) : "--"}
          </p>
          <p className="text-2xl @max-lg:text-xl">kg</p>
        </div>
        {latestWeightRecord && (
          <p className="text-note-gray mt-2 text-xs @max-md:mt-1">
            {formatJapaneseDateNonTime(latestWeightRecord.measurementDate)}時点
            <br />
            {formatDiffText(diffWeight, "kg")}
          </p>
        )}
      </div>
    </>
  );
};

// 排泄回数を計算
const DiaperGraphSummary = ({ careRecords }: { careRecords: CareRecordListResponseDto }) => {
  // 排泄記録のみ抽出
  const diaperData = careRecords.items.filter((item) => item.recordType === "DIAPER");
  // 1週間のおしっこの合計回数を算出
  const wetDiaperCount = diaperData.filter(
    (item) => item.diaperDetail?.diaperType === "WET",
  ).length;
  // 1週間のうんちの合計回数を算出
  const dirtyDiaperCount = diaperData.filter(
    (item) => item.diaperDetail?.diaperType === "DIRTY",
  ).length;
  // 1日あたりの平均おしっこの回数を算出
  const averageWetDiaperCount = (wetDiaperCount / 7).toFixed(1);
  // 1日あたりの平均うんちの回数を算出
  const averageDirtyDiaperCount = (dirtyDiaperCount / 7).toFixed(1);
  return (
    <>
      {/* 排泄回数 */}
      <div className="border-graph-border-diaper bg-translucent col-span-3 flex h-40 flex-col items-center justify-center rounded-lg border text-center backdrop-blur-[7.5px] @max-md:col-span-2 @max-md:h-30">
        <p className="text-sm @max-md:text-xs">排泄回数</p>
        <div className="mt-1 flex items-center gap-4 @max-md:mt-1">
          <div>
            <p className="text-sm">おしっこ</p>
            <div className="flex items-end gap-1">
              <p className="text-2xl font-medium @max-lg:text-xl @max-md:text-[28px]">
                {averageWetDiaperCount}
              </p>
              <p className="text-lg @max-lg:text-sm @max-md:text-lg">回/日</p>
            </div>
          </div>
          <div className="bg-line-gray h-8 w-px"></div>
          <div>
            <p className="text-sm">うんち</p>
            <div className="flex items-end gap-1">
              <p className="text-2xl font-medium @max-lg:text-xl @max-md:text-[28px]">
                {averageDirtyDiaperCount}
              </p>
              <p className="text-lg @max-lg:text-sm @max-md:text-lg">回/日</p>
            </div>
          </div>
        </div>
        <p className="text-note-gray mt-1 text-xs @max-md:mt-1">過去1週間分の集計</p>
      </div>
    </>
  );
};

// ミルク量を計算
const MilkGraphSummary = ({ careRecords }: { careRecords: CareRecordListResponseDto }) => {
  // ミルクの記録のみ抽出
  const milkData = careRecords.items.filter((item) => item.recordType === "MILK");
  // 1週間のミルク量合計を算出
  const totalMilkAmount = milkData.reduce((acc, item) => acc + (item.milkDetail?.amountMl ?? 0), 0);
  // 1日あたりの平均ミルク量を算出
  const averageMilkAmount = (totalMilkAmount / 7).toFixed(0);
  return (
    <div className="border-graph-border-milk bg-translucent col-span-2 flex h-40 flex-col items-center justify-center rounded-lg border text-center backdrop-blur-[7.5px] @max-md:col-span-1 @max-md:h-30">
      <p className="text-sm @max-md:text-xs">ミルク量</p>
      <div className="mt-2 flex items-end gap-1 @max-md:mt-1">
        <p className="text-3xl font-medium @max-lg:text-2xl @max-md:text-[28px]">
          {averageMilkAmount}
        </p>
        <p className="text-xl @max-lg:text-lg">ml/日</p>
      </div>
      <p className="text-note-gray mt-2 text-xs @max-md:mt-1">過去1週間分の集計</p>
    </div>
  );
};

// 覚えた言葉の数を表示
const WordGraphSummary = ({
  isAdmin,
  isEasy,
  wordRecords,
}: {
  isAdmin: boolean;
  isEasy: boolean;
  wordRecords: WordRecordResponseDto[];
}) => {
  return (
    <div
      className={`border-graph-border-word bg-translucent col-span-2 flex h-40 flex-col items-center justify-center rounded-lg border text-center backdrop-blur-[7.5px] @max-md:h-30 ${isAdmin && !isEasy ? "@max-md:col-span-1" : ""}`}
    >
      <p className="text-sm @max-md:text-xs">覚えた言葉の数</p>
      <div className="mt-2 flex items-end gap-1 @max-md:mt-1">
        <p className="text-3xl font-medium @max-lg:text-2xl @max-md:text-[28px]">
          {wordRecords.length}
        </p>
        <p className="text-xl @max-lg:text-lg">語</p>
      </div>
      <p className="text-note-gray mt-2 text-xs @max-md:mt-1">全期間累計</p>
    </div>
  );
};
