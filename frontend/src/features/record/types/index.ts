export type SelectedMediaData = {
  id: number;
  url: string;
};

export type RecordData = {
  id?: number;
  title: string;
  recordedDate: string;
  comment: string;
  media?: SelectedMediaData[];
};
