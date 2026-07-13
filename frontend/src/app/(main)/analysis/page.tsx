import Container from "@/components/layout/Container";
import { ChildCareNavigation } from "@/components/ui/ChildCareNavigation";
import { PageTitle } from "@/components/ui/PageTitle";
import { isAdminUser, isEasyMode } from "@/features/auth";
import { getAndBuildGraphData, GraphChart, GraphSummary } from "@/features/graph";

export default async function AnalysisPage() {
  const [isAdmin, isEasy] = await Promise.all([isAdminUser(), isEasyMode()]);
  const {
    heightData,
    weightData,
    milkData,
    diaperData,
    wordData,
    careRecords,
    growthRecords,
    wordRecords,
  } = await getAndBuildGraphData(isAdmin);

  return (
    <Container className="mt-10 @max-md:mt-5">
      <ChildCareNavigation currentPage="graph" />
      <div className="mt-10">
        <PageTitle isEasy={isEasy} text="グラフ" />
      </div>

      {/* サマリー表示 */}
      <GraphSummary
        isEasy={isEasy}
        isAdmin={isAdmin}
        growthRecords={growthRecords}
        careRecords={careRecords}
        wordRecords={wordRecords}
      />

      {/* 各種グラフ */}
      <div className="mt-15 grid gap-10 @max-md:gap-5">
        {/* 身長データグラフ */}
        <GraphChart data={heightData} variant="height" />

        {/* 体重データグラフ */}
        <GraphChart data={weightData} variant="weight" />

        {isAdmin && (
          <div className="grid grid-cols-2 gap-10 @max-md:grid-cols-1 @max-md:gap-5">
            {/* ミルクデータグラフ */}
            <GraphChart data={milkData} variant="milk" />

            {/* 排泄データグラフ */}
            <GraphChart data={diaperData} variant="diaper" />
          </div>
        )}

        {/* ことばデータグラフ */}
        <GraphChart data={wordData} variant="word" />
      </div>
    </Container>
  );
}
