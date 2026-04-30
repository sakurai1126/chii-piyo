import BackGround from "@/components/layout/BackGround";
import BottomNavigation from "@/components/layout/BottomNavigation";
import Footer from "@/components/layout/Footer";
import Header from "@/components/layout/Header";

export default function MainLayout({ children }: Readonly<{ children: React.ReactNode }>) {
  return (
    <BackGround>
      <Header />
      {children}
      <BottomNavigation />
      <Footer />
    </BackGround>
  );
}
