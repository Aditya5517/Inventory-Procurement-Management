"use client";

import { useEffect, useState } from "react";
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
  const [productCount, setProductCount] = useState(0);
  const [lowStockCount, setLowStockCount] = useState(0);
  const [pendingRequestCount, setPendingRequestCount] = useState(0);
  const [supplierCount, setSupplierCount] = useState(0);

  const [recentRequests, setRecentRequests] =
    useState<PurchaseRequest[]>([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    loadDashboard();
  }, []);

  async function loadDashboard() {
    try {
      setLoading(true);
      setError("");

      // Load all dashboard data together
      const [products, suppliers, requests] = await Promise.all([
        apiRequest("/api/products"),
        apiRequest("/api/suppliers"),
        apiRequest("/api/purchase-requests"),
      ]);

      const productList: Product[] = products || [];
      const supplierList: Supplier[] = suppliers || [];
      const requestList: PurchaseRequest[] = requests || [];

      setProductCount(productList.length);

      setLowStockCount(
        productList.filter(
          (product) =>
            product.currentStock < product.reorderLevel
        ).length
      );

      setSupplierCount(
        supplierList.filter(
          (supplier) => supplier.isActive
        ).length
      );

      setPendingRequestCount(
        requestList.filter(
          (request) => request.status === "PENDING"
        ).length
      );

      setRecentRequests(
        [...requestList]
          .reverse()
          .slice(0, 5)
      );
    } catch (error) {
      console.error("Dashboard loading error:", error);

      setError(
        "Could not load dashboard data. Please refresh the page."
      );
    } finally {
      setLoading(false);
    }
  }

  if (loading) {
    return (
      <AppLayout>
        <h1 className="text-3xl font-bold text-black">
          Dashboard
        </h1>

        <p className="mt-6 text-gray-600">
          Loading dashboard...
        </p>
      </AppLayout>
    );
  }

  return (
    <AppLayout>
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-black">
            Dashboard
          </h1>

          <p className="mt-2 text-gray-600">
            A quick overview of inventory and procurement.
          </p>
        </div>

        <button
          onClick={loadDashboard}
          className="rounded border border-gray-300 bg-white px-4 py-2 text-sm text-black hover:bg-gray-100"
        >
          Refresh
        </button>
      </div>

      {error && (
        <div className="mt-6 rounded border border-red-200 bg-red-50 p-4">
          <p className="text-sm text-red-600">
            {error}
          </p>
        </div>
      )}

      <div className="mt-8 grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-4">
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

      <div className="mt-8 overflow-hidden rounded-lg border border-gray-200 bg-white">
        <div className="border-b border-gray-200 p-4">
          <h2 className="text-lg font-bold text-black">
            Recent Purchase Requests
          </h2>
        </div>

        {recentRequests.length > 0 ? (
          <div className="overflow-x-auto">
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
                      className="border-t border-gray-200"
                    >
                      <td className="p-3">
                        {request.requestNumber}
                      </td>

                      <td className="p-3">
                        {request.productName}
                      </td>

                      <td className="p-3">
                        {request.requesterEmail}
                      </td>

                      <td className="p-3">
                        <StatusText
                          status={request.status}
                        />
                      </td>
                    </tr>
                  )
                )}
              </tbody>
            </table>
          </div>
        ) : (
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
    <div className="rounded-lg border border-gray-200 bg-white p-5">
      <p className="text-sm text-gray-500">
        {title}
      </p>

      <p className="mt-2 text-3xl font-bold text-black">
        {value}
      </p>
    </div>
  );
}

function StatusText({
  status,
}: {
  status: string;
}) {
  if (status === "APPROVED") {
    return (
      <span className="font-medium text-green-600">
        APPROVED
      </span>
    );
  }

  if (status === "REJECTED") {
    return (
      <span className="font-medium text-red-600">
        REJECTED
      </span>
    );
  }

  return (
    <span className="font-medium text-yellow-600">
      PENDING
    </span>
  );
}