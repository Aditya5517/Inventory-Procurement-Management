"use client";

import {
  useEffect,
  useState,
} from "react";

import AppLayout from "@/components/AppLayout";
import { apiRequest } from "@/lib/api";

type Product = {
  id: number;
  currentStock: number;
  reorderLevel: number;
};

type Supplier = {
  id: number;
  isActive: boolean;
};

type PurchaseRequest = {
  id: number;
  requestNumber: string;
  productName: string;
  requesterEmail: string;
  status: string;
};

export default function DashboardPage() {
  const [productCount, setProductCount] =
    useState(0);

  const [
    lowStockCount,
    setLowStockCount,
  ] = useState(0);

  const [
    pendingRequestCount,
    setPendingRequestCount,
  ] = useState(0);

  const [
    supplierCount,
    setSupplierCount,
  ] = useState(0);

  const [
    recentRequests,
    setRecentRequests,
  ] = useState<PurchaseRequest[]>([]);

  useEffect(() => {
    loadDashboard();
  }, []);

  async function loadDashboard() {
    try {
      const products: Product[] =
        await apiRequest(
          "/api/products"
        );

      const suppliers: Supplier[] =
        await apiRequest(
          "/api/suppliers"
        );

      const requests: PurchaseRequest[] =
        await apiRequest(
          "/api/purchase-requests"
        );

      setProductCount(
        products.length
      );

      setLowStockCount(
        products.filter(
          (product) =>
            product.currentStock <
            product.reorderLevel
        ).length
      );

      setSupplierCount(
        suppliers.filter(
          (supplier) =>
            supplier.isActive
        ).length
      );

      setPendingRequestCount(
        requests.filter(
          (request) =>
            request.status ===
            "PENDING"
        ).length
      );

      setRecentRequests(
        requests.slice(0, 5)
      );
    } catch (error) {
      console.error(error);
    }
  }

  return (
    <AppLayout>
      <h1 className="text-3xl font-bold text-black">
        Dashboard
      </h1>

      <p className="mt-2 text-gray-600">
        A quick overview of inventory
        and procurement.
      </p>

      <div className="mt-8 grid grid-cols-1 gap-4 md:grid-cols-4">
        <Card
          title="Total Products"
          value={productCount}
        />

        <Card
          title="Low Stock Items"
          value={lowStockCount}
        />

        <Card
          title="Pending Requests"
          value={pendingRequestCount}
        />

        <Card
          title="Active Suppliers"
          value={supplierCount}
        />
      </div>

      <div className="mt-8 overflow-hidden rounded-lg border bg-white">
        <div className="border-b p-4">
          <h2 className="text-lg font-bold text-black">
            Recent Purchase Requests
          </h2>
        </div>

        <table className="w-full text-left">
          <thead className="bg-gray-100 text-black">
            <tr>
              <th className="p-3">
                Request
              </th>

              <th className="p-3">
                Product
              </th>

              <th className="p-3">
                Requested By
              </th>

              <th className="p-3">
                Status
              </th>
            </tr>
          </thead>

          <tbody className="text-gray-700">
            {recentRequests.map(
              (request) => (
                <tr
                  key={request.id}
                  className="border-t"
                >
                  <td className="p-3">
                    {
                      request.requestNumber
                    }
                  </td>

                  <td className="p-3">
                    {
                      request.productName
                    }
                  </td>

                  <td className="p-3">
                    {
                      request.requesterEmail
                    }
                  </td>

                  <td className="p-3">
                    {request.status}
                  </td>
                </tr>
              )
            )}
          </tbody>
        </table>

        {recentRequests.length === 0 && (
          <p className="p-6 text-gray-500">
            No purchase requests yet.
          </p>
        )}
      </div>
    </AppLayout>
  );
}

function Card({
  title,
  value,
}: {
  title: string;
  value: number;
}) {
  return (
    <div className="rounded-lg border bg-white p-5">
      <p className="text-gray-500">
        {title}
      </p>

      <p className="mt-2 text-3xl font-bold text-black">
        {value}
      </p>
    </div>
  );
}