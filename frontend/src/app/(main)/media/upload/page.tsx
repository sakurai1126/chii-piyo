"use client";

import Container from "@/components/layout/Container";
import PageTitle from "@/components/ui/PageTitle";
import { ImageUploader, VideoUploader, MultipleSettings, FileList } from "@/features/upload";

export default function UploadPage() {
  return (
    <Container className="mt-20 max-md:mt-5">
      <PageTitle text="アップロード" />
      <div className="mt-15 grid grid-cols-2 gap-10 max-lg:gap-3 max-md:mt-6">
        <ImageUploader />
        <VideoUploader />
      </div>

      <MultipleSettings />
      <FileList />
    </Container>
  );
}
