import Container from "@/components/layout/Container";
import { ChildCareNavigation } from "@/components/ui/ChildCareNavigation";
import PageTitle from "@/components/ui/PageTitle";
import { GraphChart } from "@/features/graph";
import { GraphSummary } from "@/features/graph";
import { formatJapaneseDateNonTime } from "@/utils/date";

export default async function AnalysisPage() {
  // モック定義
  const heightData = [
    { month: formatJapaneseDateNonTime("2026-05-01"), standardRange: [44.0, 52.6], value: 48.0 },
    { month: formatJapaneseDateNonTime("2026-06-01"), standardRange: [50.0, 58.4], value: 53.5 },
    { month: formatJapaneseDateNonTime("2026-07-01"), standardRange: [53.3, 61.7], value: 58.0 },
    { month: formatJapaneseDateNonTime("2026-08-01"), standardRange: [55.9, 64.5], value: 58.0 },
  ];

  return (
    <Container className="mt-10 max-md:mt-5">
      <ChildCareNavigation currentPage="graph" />
      <div className="mt-10">
        <PageTitle text="グラフ" />
      </div>

      {/* サマリー表示 */}
      <GraphSummary />

      {/* 各種グラフ */}
      <div className="mt-15 grid gap-10 max-md:gap-5">
        <GraphChart
          data={heightData}
          title="身長推移"
          graphHeight={400}
          color="#FF4F4F"
          unit="cm"
          chartType="growth"
        />
        <GraphChart
          data={heightData}
          title="体重推移"
          graphHeight={400}
          color="#D1CB32"
          unit="kg"
          chartType="growth"
        />
        <div className="grid grid-cols-2 gap-10 max-md:grid-cols-1 max-md:gap-5">
          <GraphChart
            data={heightData}
            title="ミルク量"
            graphHeight={300}
            color="#4ADB26"
            unit="ml"
            chartType="bar"
          />
          <GraphChart
            data={heightData}
            title="排泄回数"
            graphHeight={300}
            color="#26B5DB"
            unit="回"
            chartType="bar"
          />
        </div>
        <GraphChart
          data={heightData}
          title="覚えた言葉の数"
          graphHeight={300}
          color="#DB5926"
          unit="語"
          chartType="line"
        />
      </div>
    </Container>
  );
}
