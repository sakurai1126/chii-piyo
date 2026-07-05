import {
  CareRecordListResponseDto,
  GrowthRecordResponseDto,
  WordRecordResponseDto,
} from "@/lib/api-client/gen";
import { formatJapaneseDateNonTime } from "@/utils/date";

type Props = {
  growthRecords: GrowthRecordResponseDto[];
  careRecords: CareRecordListResponseDto;
  wordRecords: WordRecordResponseDto[];
};

export const GraphSummary = ({ growthRecords, careRecords, wordRecords }: Props) => {
  return (
    <div className="mt-10 grid grid-cols-11 gap-3 max-md:mt-6 max-md:grid-cols-2 max-md:gap-1.5">
      <GrowthGraphSummary growthRecords={growthRecords} />
      <DiaperGraphSummary careRecords={careRecords} />
      <MilkGraphSummary careRecords={careRecords} />
      <WordGraphSummary wordRecords={wordRecords} />
    </div>
  );
};

// 成長記録を計算
const GrowthGraphSummary = ({ growthRecords }: { growthRecords: GrowthRecordResponseDto[] }) => {
  // 最新の身長データを取得
  const latestHeightRecord = growthRecords.find((record) => record.height != null);
  const latestHeight = latestHeightRecord?.height;

  // 最新の体重データを取得
  const latestWeightRecord = growthRecords.find((record) => record.weight != null);
  const latestWeight = latestWeightRecord?.weight;

  // 差分表示用変数を定義
  let diffHeight: string | null = null;
  let diffWeight: string | null = null;

  // 最新の身長データが存在する場合前月データを算出
  if (latestHeight != null && latestHeightRecord) {
    // 最新の身長データの記録日時
    const latestDate = new Date(latestHeightRecord.measurementDate);

    // 最新データの1ヶ月前の年月
    const prevMonth = new Date(latestDate.getFullYear(), latestDate.getMonth() - 1);

    // 記録が前月のデータに一致するものを取得
    const prevMonthHeightRecord = growthRecords.find((record) => {
      if (record.height == null) return false;
      const targetDate = new Date(record.measurementDate);
      return (
        targetDate.getFullYear() === prevMonth.getFullYear() &&
        targetDate.getMonth() === prevMonth.getMonth()
      );
    })?.height;

    // 取得できた場合差分変数を更新
    if (prevMonthHeightRecord != null) {
      diffHeight = (latestHeight - prevMonthHeightRecord).toFixed(1);
    }
  }

  // 最新の体重データが存在する場合前月データを算出
  if (latestWeight != null && latestWeightRecord) {
    // 最新の体重データの記録日時
    const latestDate = new Date(latestWeightRecord.measurementDate);

    // 最新データの1ヶ月前の年月
    const prevMonth = new Date(latestDate.getFullYear(), latestDate.getMonth() - 1);

    // 記録が前月のデータに一致するものを取得
    const prevMonthWeightRecord = growthRecords.find((record) => {
      if (record.weight == null) return false;
      const targetDate = new Date(record.measurementDate);
      return (
        targetDate.getFullYear() === prevMonth.getFullYear() &&
        targetDate.getMonth() === prevMonth.getMonth()
      );
    })?.weight;

    // 取得できた場合差分変数を更新
    if (prevMonthWeightRecord != null) {
      diffWeight = (latestWeight - prevMonthWeightRecord).toFixed(1);
    }
  }

  return (
    <>
      {/* 身長 */}
      <div className="border-graph-border-height col-span-2 flex h-40 flex-col items-center justify-center rounded-lg border bg-white/50 text-center backdrop-blur-[7.5px] max-md:col-span-1 max-md:h-30">
        <p className="max-md:text-[13px]">身長</p>
        <div className="mt-2 flex items-end gap-1 max-md:mt-1">
          <p className="text-3xl font-medium max-lg:text-2xl max-md:text-[28px]">
            {latestHeight ? latestHeight.toFixed(1) : "--"}
          </p>
          <p className="text-2xl max-lg:text-xl">cm</p>
        </div>
        {latestHeightRecord && (
          <>
            <p className="text-note-gray mt-2 text-xs max-md:mt-1">
              {formatJapaneseDateNonTime(latestHeightRecord.measurementDate)}時点
              <br />
              {diffHeight != null
                ? `前月比 ${Number(diffHeight) > 0 ? "+" : ""}${diffHeight}cm`
                : "前月記録なし"}
            </p>
          </>
        )}
      </div>

      {/* 体重 */}
      <div className="border-graph-border-weight col-span-2 flex h-40 flex-col items-center justify-center rounded-lg border bg-white/50 text-center backdrop-blur-[7.5px] max-md:col-span-1 max-md:h-30">
        <p className="max-md:text-[13px]">体重</p>
        <div className="mt-2 flex items-end gap-1 max-md:mt-1">
          <p className="text-3xl font-medium max-lg:text-2xl max-md:text-[28px]">
            {latestWeight ? latestWeight.toFixed(1) : "--"}
          </p>
          <p className="text-2xl max-lg:text-xl">kg</p>
        </div>
        {latestWeightRecord && (
          <>
            <p className="text-note-gray mt-2 text-xs max-md:mt-1">
              {formatJapaneseDateNonTime(latestWeightRecord.measurementDate)}時点
              <br />
              {diffWeight != null
                ? `前月比 ${Number(diffWeight) > 0 ? "+" : ""}${diffWeight}kg`
                : "前月記録なし"}
            </p>
          </>
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
      <div className="border-graph-border-diaper col-span-3 flex h-40 flex-col items-center justify-center rounded-lg border bg-white/50 text-center backdrop-blur-[7.5px] max-md:col-span-2 max-md:h-30">
        <p className="text-sm max-md:text-xs">排泄回数</p>
        <div className="mt-1 flex items-center gap-4 max-md:mt-1">
          <div>
            <p className="text-sm">おしっこ</p>
            <div className="flex items-end gap-1">
              <p className="text-2xl font-medium max-lg:text-xl max-md:text-[28px]">
                {averageWetDiaperCount}
              </p>
              <p className="text-lg max-lg:text-sm max-md:text-lg">回/日</p>
            </div>
          </div>
          <div className="bg-line-gray h-8 w-px"></div>
          <div>
            <p className="text-sm">うんち</p>
            <div className="flex items-end gap-1">
              <p className="text-2xl font-medium max-lg:text-xl max-md:text-[28px]">
                {averageDirtyDiaperCount}
              </p>
              <p className="text-lg max-lg:text-sm max-md:text-lg">回/日</p>
            </div>
          </div>
        </div>
        <p className="text-note-gray mt-1 text-xs max-md:mt-1">過去1週間分の集計</p>
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
    <>
      {/* ミルク量 */}
      <div className="border-graph-border-milk col-span-2 flex h-40 flex-col items-center justify-center rounded-lg border bg-white/50 text-center backdrop-blur-[7.5px] max-md:col-span-1 max-md:h-30">
        <p className="text-sm max-md:text-xs">ミルク量</p>
        <div className="mt-2 flex items-end gap-1 max-md:mt-1">
          <p className="text-3xl font-medium max-lg:text-2xl max-md:text-[28px]">
            {averageMilkAmount}
          </p>
          <p className="text-xl max-lg:text-lg">ml/日</p>
        </div>
        <p className="text-note-gray mt-2 text-xs max-md:mt-1">過去1週間分の集計</p>
      </div>
    </>
  );
};

// 覚えた言葉の数を表示
const WordGraphSummary = ({ wordRecords }: { wordRecords: WordRecordResponseDto[] }) => {
  return (
    <>
      {/* 覚えた言葉の数 */}
      <div className="border-graph-border-word col-span-2 flex h-40 flex-col items-center justify-center rounded-lg border bg-white/50 text-center backdrop-blur-[7.5px] max-md:col-span-1 max-md:h-30">
        <p className="text-sm max-md:text-xs">覚えた言葉の数</p>
        <div className="mt-2 flex items-end gap-1 max-md:mt-1">
          <p className="text-3xl font-medium max-lg:text-2xl max-md:text-[28px]">
            {wordRecords.length}
          </p>
          <p className="text-xl max-lg:text-lg">語</p>
        </div>
        <p className="text-note-gray mt-2 text-xs max-md:mt-1">全期間累計</p>
      </div>
    </>
  );
};
