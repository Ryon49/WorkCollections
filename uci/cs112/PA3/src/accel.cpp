/*
Name: Weifeng Dong
Studnet ID: 26027368
*/

#include <nori/accel.h>
#include <Eigen/Geometry>

#include <numeric>
#include <vector>
#include <set>
#include <memory>
#include <tbb/blocked_range.h>
#include <tbb/parallel_for.h>
#include <tbb/parallel_do.h>

NORI_NAMESPACE_BEGIN

void Accel::addMesh(Mesh *mesh) {
    if (m_mesh)
        throw NoriException("Accel: only a single mesh is supported!");
    m_mesh = mesh;
    m_bbox = m_mesh->getBoundingBox();

    std::vector<uint32_t> indexes = std::vector<uint32_t>(m_mesh->getTriangleCount());
    std::iota(std::begin(indexes), std::end(indexes), 0);

    if (false) {
        // for testing purpose.
        std::cout << m_bbox.toString() << "\n";
        std::cout << "center: " << m_bbox.getCenter().toString() << "\n";
        std::cout << "0: " << m_bbox.getCorner(0).toString() << "\n";
        std::cout << "1: " << m_bbox.getCorner(1).toString() << "\n";
        std::cout << "2: " << m_bbox.getCorner(2).toString() << "\n";
        std::cout << "3: " << m_bbox.getCorner(3).toString() << "\n";
        std::cout << "4: " << m_bbox.getCorner(4).toString() << "\n";
        std::cout << "5: " << m_bbox.getCorner(5).toString() << "\n";
        std::cout << "6: " << m_bbox.getCorner(6).toString() << "\n";
        std::cout << "7: " << m_bbox.getCorner(7).toString() << "\n";
    }

    m_root = buildNode(m_bbox, indexes);
}

void Accel::build() {
    /* Nothing to do here for now */
}

std::unique_ptr<Accel::Node> Accel::buildNode(BoundingBox3f bbox, std::vector<uint32_t> &indexes) {
    Point3f diff = bbox.getExtents();
    if (diff.x() < 0.01f || diff.y() < 0.01f || diff.z() < 0.01f) {
        // return a leaf
        return std::make_unique<Accel::Node>(bbox, std::vector<uint32_t>(std::begin(indexes), std::end(indexes)));
    }

    std::unique_ptr<Accel::Node> root = std::make_unique<Accel::Node>(bbox);

    // maybe this part is wrong, instead I should create a map of (corner, indexes) map here
    for (uint32_t index : indexes) {
        if (m_mesh->getBoundingBox(index).overlaps(bbox)) {
            root->add(index);
        }
    }
    
    if (root->size() < 10) {
        return root;
    }

    // Parallel split
    tbb::blocked_range<int> range(0, 8);
    Point3f center = bbox.getCenter();
    auto map = [&](const tbb::blocked_range<int> &range) {
        for (int i = std::begin(range); i < std::end(range); i++) {
            BoundingBox3f sub = createBBox(center, bbox.getCorner(i));
            auto child = buildNode(sub, root->indexes());
            root->children().push_back(std::move(child));
        }
    };
    tbb::parallel_for(range, map);

    // sequential split
    // Point3f center = bbox.getCenter();
    // for (int i = 0; i < 8; i++) {
    //     Point3f corner = bbox.getCorner(i);
    //     BoundingBox3f sub = createBBox(center, corner, i);

    //     root->children().push_back(buildNode(sub, root->indexes()));
    // }

    // we should be able to free indexes for parent, since it won't be used anymore.

    return root;
}

bool Accel::rayIntersect(const Ray3f &ray_, Intersection &its, bool shadowRay) const {
    bool foundIntersection = false;  // Was an intersection found so far?
    uint32_t f = (uint32_t) -1;      // Triangle index of the closest intersection

    Ray3f ray(ray_); /// Make a copy of the ray (we will need to update its '.maxt' value)

    std::set<uint32_t> set;
    traverse(m_root, ray, &set);
    
    for (auto &idx : set) {
        float u, v, t;
        if (m_mesh->rayIntersect(idx, ray, u, v, t)) {
            /* An intersection was found! Can terminate
               immediately if this is a shadow ray query */
            if (shadowRay)
                return true;
            ray.maxt = its.t = t;
            its.uv = Point2f(u, v);
            its.mesh = m_mesh;
            f = idx;
            foundIntersection = true;
        }
    }

    // /* Brute force search through all triangles */
    // for (uint32_t idx = 0; idx < m_mesh->getTriangleCount(); ++idx) {
    //     float u, v, t;
    //     if (m_mesh->rayIntersect(idx, ray, u, v, t)) {
    //         count += 1;
    //         /* An intersection was found! Can terminate
    //            immediately if this is a shadow ray query */
    //         if (shadowRay)
    //             return true;
    //         ray.maxt = its.t = t;
    //         its.uv = Point2f(u, v);
    //         its.mesh = m_mesh;
    //         f = idx;
    //         foundIntersection = true;
    //     }
    // }

    if (foundIntersection) {
        /* At this point, we now know that there is an intersection,
           and we know the triangle index of the closest such intersection.

           The following computes a number of additional properties which
           characterize the intersection (normals, texture coordinates, etc..)
        */

        /* Find the barycentric coordinates */
        Vector3f bary;
        bary << 1-its.uv.sum(), its.uv;

        /* References to all relevant mesh buffers */
        const Mesh *mesh   = its.mesh;
        const MatrixXf &V  = mesh->getVertexPositions();
        const MatrixXf &N  = mesh->getVertexNormals();
        const MatrixXf &UV = mesh->getVertexTexCoords();
        const MatrixXu &F  = mesh->getIndices();

        /* Vertex indices of the triangle */
        uint32_t idx0 = F(0, f), idx1 = F(1, f), idx2 = F(2, f);

        Point3f p0 = V.col(idx0), p1 = V.col(idx1), p2 = V.col(idx2);

        /* Compute the intersection positon accurately
           using barycentric coordinates */
        its.p = bary.x() * p0 + bary.y() * p1 + bary.z() * p2;

        /* Compute proper texture coordinates if provided by the mesh */
        if (UV.size() > 0)
            its.uv = bary.x() * UV.col(idx0) +
                bary.y() * UV.col(idx1) +
                bary.z() * UV.col(idx2);

        /* Compute the geometry frame */
        its.geoFrame = Frame((p1-p0).cross(p2-p0).normalized());

        if (N.size() > 0) {
            /* Compute the shading frame. Note that for simplicity,
               the current implementation doesn't attempt to provide
               tangents that are continuous across the surface. That
               means that this code will need to be modified to be able
               use anisotropic BRDFs, which need tangent continuity */

            its.shFrame = Frame(
                (bary.x() * N.col(idx0) +
                 bary.y() * N.col(idx1) +
                 bary.z() * N.col(idx2)).normalized());
        } else {
            its.shFrame = its.geoFrame;
        }
    }

    return foundIntersection;
}

bool Accel::intersect(BoundingBox3f bbox, int index) {
    // not used, replaced by native bbox.overlaps()
    const Point3f p0 = m_mesh->getVertexPositions().col(m_mesh->getIndices()(0, index));
    const Point3f p1 = m_mesh->getVertexPositions().col(m_mesh->getIndices()(1, index));
    const Point3f p2 = m_mesh->getVertexPositions().col(m_mesh->getIndices()(2, index));
    return bbox.contains(p0) || bbox.contains(p1) || bbox.contains(p2);
}

BoundingBox3f Accel::createBBox(Point3f p1, Point3f p2) {
    Point3f min = Point3f(std::min(p1.x(), p2.x()), std::min(p1.y(), p2.y()), std::min(p1.z(), p2.z()));
    Point3f max = Point3f(std::max(p1.x(), p2.x()), std::max(p1.y(), p2.y()), std::max(p1.z(), p2.z()));
    return BoundingBox3f(min, max);
}

/**
 * Use set here because triangle face can overlap multiple tree segments.
*/
void Accel::traverse(const std::unique_ptr<Accel::Node> &root, const Ray3f &ray, std::set<uint32_t> *set) const {
    if (root->isLeaf() && root->size() > 0 && root->getBoundingBox().rayIntersect(ray)) {
        set->insert(std::begin(root->indexes()), std::end(root->indexes()));
        return;
    }

    // for part 3
    auto comparator = [&ray](const std::unique_ptr<Node> &p1, const std::unique_ptr<Node> &p2) {
        auto t1 = p1->getBoundingBox().distanceTo(ray.o);
        auto t2 = p2->getBoundingBox().distanceTo(ray.o);
        return t1 < t2;
    };
    std::sort(std::begin(root->children()), std::end(root->children()), comparator);

    // Idea:
    // After sorting based on distance, I believe children cells will behave like
    // (1) "Intersect" -- .. - "Intersect" -- "Not intersect" -- .. -- "Not intersect"
    // (2) "Not intersect" -- "Intersect" -- .... - "Intersect" -- "Not intersect" -- ..
    // (3) "Not intersect" -- .. -- "Not intersect" -- "Intersect" -- .. -- "Intersect"

    // And we want to optimize condition (1) and (2)

    bool found = false;
    bool end = false;
    for (auto &child : root->children()) {
        if (child->getBoundingBox().rayIntersect(ray)) {
            traverse(child, ray, set);
            found = true;
        } else if (found) {
            end = true;
        } else if (end) {
            break;
        }
    }
}

NORI_NAMESPACE_END

