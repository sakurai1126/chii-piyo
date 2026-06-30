import { notFound } from "next/navigation";

import Container from "@/components/layout/Container";
import { formatJapaneseDateBasic, formatJapaneseDateNonTime } from "@/utils/date";

import { getCareRecords } from "../api/getCareRecords";
import { getGrowthRecords } from "../api/getGrowthRecords";

import { CareTimeLineItem } from "./CareTimeLineItem";

type Props = {
  date: string;
};

export const CareTimeLine = async ({ date }: Props) => {
  // yyyy-mm-dd 形式の文字列かチェック
  const isFormatValid = /^\d{4}-\d{2}-\d{2}$/.test(date);

  // 実在する有効な日付かチェック
  const isDateValid =
    !Number.isNaN(new Date(date).getTime()) && formatJapaneseDateBasic(new Date(date)) === date;

  // 日付として不正な場合は404を表示する
  if (!isFormatValid || !isDateValid) {
    notFound();
  }

  // 日付を取得
  const startDate = new Date(date);
  const endDate = new Date(startDate.getFullYear(), startDate.getMonth(), startDate.getDate() + 1);

  // データを取得
  const careRecords = await getCareRecords({ startDate, endDate });
  const growthRecords = await getGrowthRecords({ startDate, endDate });

  return (
    <Container className="pt-20 pb-30 max-md:pt-10 max-md:pb-20">
      <div className="pl-11 max-md:pl-8">
        <p className="mb-1 text-lg font-medium max-md:text-sm">
          {formatJapaneseDateNonTime(startDate)}のタイムライン
        </p>
        <a href={"/care"} className="ml-auto text-sm underline max-md:text-xs">
          一覧に戻る
        </a>
      </div>
      <div className="mt-5">
        {growthRecords?.map((item, index) => {
          return <CareTimeLineItem key={item.id} index={index} growthItem={item} />;
        })}
        {careRecords.items?.map((item, index) => {
          return <CareTimeLineItem key={item.id} index={index} careItem={item} />;
        })}
      </div>
    </Container>
  );
};
