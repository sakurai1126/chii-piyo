"use client";

import { useSearchParams } from "next/navigation";

import { PageTitle } from "@/components/ui/PageTitle";

type Props = {
  isEasy: boolean;
};

export const MediaTitle = ({ isEasy }: Props) => {
  const searchParams = useSearchParams();
  const mediaKind = searchParams.get("mediaKind") as "PHOTO" | "VIDEO";

  let title = "写真・動画一覧";

  if (mediaKind === "PHOTO") {
    title = "写真一覧";
  }

  if (mediaKind === "VIDEO") {
    title = "動画一覧";
  }

  return <PageTitle isEasy={isEasy} text={title} />;
};
