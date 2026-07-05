import Container from "@/components/layout/Container";
import { ChildCareNavigation } from "@/components/ui/ChildCareNavigation";
import PageTitle from "@/components/ui/PageTitle";
import { getAndBuildGraphData, GraphChart } from "@/features/graph";
import { GraphSummary } from "@/features/graph";

export default async function AnalysisPage() {
  const {
    heightData,
    weightData,
    milkData,
    diaperData,
    wordData,
    careRecords,
    growthRecords,
    wordRecords,
  } = await getAndBuildGraphData();

  return (
    <Container className="mt-10 max-md:mt-5">
      <ChildCareNavigation currentPage="graph" />
      <div className="mt-10">
        <PageTitle text="グラフ" />
      </div>

      {/* サマリー表示 */}
      <GraphSummary
        growthRecords={growthRecords}
        careRecords={careRecords}
        wordRecords={wordRecords}
      />

      {/* 各種グラフ */}
      <div className="mt-15 grid gap-10 max-md:gap-5">
        {/* 身長データグラフ */}
        <GraphChart data={heightData} variant="height" />

        {/* 体重データグラフ */}
        <GraphChart data={weightData} variant="weight" />

        <div className="grid grid-cols-2 gap-10 max-md:grid-cols-1 max-md:gap-5">
          {/* ミルクデータグラフ */}
          <GraphChart data={milkData} variant="milk" />

          {/* 排泄データグラフ */}
          <GraphChart data={diaperData} variant="diaper" />
        </div>
        {/* ことばデータグラフ */}
        <GraphChart data={wordData} variant="word" />
      </div>
    </Container>
  );
}
